package com.example.backend.logic.caracteristica;

import com.example.backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static java.util.stream.Collectors.joining;

@org.springframework.stereotype.Service
public class ServiceC {
    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    public Iterable<Caracteristica> caracteristicaFindAll () {
        return caracteristicaRepository.findAll();
    }

    public List<Caracteristica> findRoots() {
        return caracteristicaRepository.findByPadreIsNull();
    }

    public List<Caracteristica> findHijos(Caracteristica padre) {
        return caracteristicaRepository.findByPadre(padre);
    }


    //CAMBIOS.
    private void agregarConLosDescendientes (Set <Integer> set, Caracteristica caracteristica)
    {
        set.add(caracteristica.getId());
        for (Caracteristica hijo : findHijos(caracteristica))
        {
            agregarConLosDescendientes(set, hijo);
        }
    }

    public Caracteristica findById(Integer id)
    {
        if (id == null) return null;
        Optional<Caracteristica> opt = caracteristicaRepository.findById(id);
        return opt.orElse(null);
    }

    public List<Caracteristica> buildRuta(Caracteristica actual)
    {
        List<Caracteristica> ruta = new ArrayList<>();
        Caracteristica cursor = actual;

        while (cursor != null)
        {
            ruta.add(0, cursor);
            cursor = cursor.getPadre();
        }
        return ruta;
    }


    public List<Integer> expandirConDescendientes(List<Integer> ids)
    {
        Set<Integer> todos = new LinkedHashSet<>();
        for (Integer id : ids)
        {
            Caracteristica c = findById(id);
            if (c != null)
                agregarConLosDescendientes(todos, c);
        }
        return new ArrayList<>(todos);
    }

    private void calcularNiveles (Map<Integer, Integer> niveles, Caracteristica c, int nivel)
    {
        niveles.put(c.getId(), nivel);
        for (Caracteristica hijo : findHijos(c))
        {
            calcularNiveles(niveles, hijo, nivel+1);
        }
    }


    //HOLA HOLA.

    private void agregarEnOrden(List <Caracteristica> lista, Caracteristica c)
    {
        lista.add(c);
        for (Caracteristica hijo : findHijos (c))
        {
            agregarEnOrden(lista, hijo);
        }
    }

    public List<Caracteristica> getArbolOrdenado()
    {
        List<Caracteristica> lista = new ArrayList<>();
        for (Caracteristica raiz : findRoots())
        {
            agregarEnOrden(lista, raiz);
        }
        return lista;
    }

    public String buildRutaString(Caracteristica c)
    {
        return buildRuta(c).stream().map(Caracteristica::getNombre).collect(joining("/"));
    }

    public void crearCaracteristica(String nombre, Integer padreId)
    {
        String nombreLimpio = nombre.trim();

        if (nombreLimpio.isEmpty())
            throw new IllegalArgumentException("El nombre de la característica NO puede estar vacío.");

        if (nombreLimpio.length() < 2)
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");

        if (!nombreLimpio.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s#&*()^]+$"))
            throw new IllegalArgumentException("El nombre SOLO puede contener letras y los símbolos # & * ( ) ^");

        if (!nombreLimpio.matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*"))
            throw new IllegalArgumentException("El nombre debe contener al menos UNA letra.");

        Caracteristica padre = findById(padreId);
        boolean existe;

        if (padre == null)
        {
            existe = caracteristicaRepository.findByNombreIgnoreCaseAndPadreIsNull(nombreLimpio).isPresent();
        }
        else
        {
            existe = caracteristicaRepository.findByNombreIgnoreCaseAndPadre(nombreLimpio, padre).isPresent();
        }

        if (existe)
        {
            String nivel = (padre == null) ? "las raíces" : "\"" + padre.getNombre() + "\"";
            throw new IllegalArgumentException("Ya existe una característica llamada \"" + nombreLimpio + "\" en " + nivel + ".");
        }

        Caracteristica c = new Caracteristica();
        c.setNombre(nombreLimpio);
        c.setPadre(padre);
        caracteristicaRepository.save(c);
    }

    public Map<Integer, Integer> getNivelesArbol()
    {
        Map<Integer, Integer> niveles = new LinkedHashMap<>();
        for (Caracteristica raiz : findRoots())
        {
            calcularNiveles(niveles, raiz, 0);
        }
        return niveles;
    }

}
