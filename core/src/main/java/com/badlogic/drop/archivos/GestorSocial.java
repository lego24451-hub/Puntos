package com.badlogic.drop.archivos;

import com.badlogic.drop.config.Reto;
import com.badlogic.drop.config.SolicitudAmistad;
import com.badlogic.drop.config.Usuarios;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


 // Maneja la persistencia de solicitudes de amistad y retos.
 

 
public class GestorSocial {

    private static final String RUTA_BASE = "usuarios/";

   
    //  SOLICITUDES DE AMISTAD
    

    //Devuelve todas las solicitudes almacenadas para un usuario. 
    @SuppressWarnings("unchecked")
    public static List<SolicitudAmistad> cargarSolicitudes(String username) {
        String ruta = RUTA_BASE + username + "/solicitudes.bin";
        File f = new File(ruta);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<SolicitudAmistad>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error cargando solicitudes de " + username + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Reescribe la lista completa de solicitudes de un usuario. 
    public static void guardarSolicitudes(String username, List<SolicitudAmistad> lista) {
        GestorArchivos.crearCarpetaUsuario(username);
        String ruta = RUTA_BASE + username + "/solicitudes.bin";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.out.println("Error guardando solicitudes de " + username + ": " + e.getMessage());
        }
    }

    
     // Envía una solicitud de amistad de {@code de} hacia {@code para}.
     //Devuelve false si ya existe una solicitud pendiente o ya son amigos.
     
    public static boolean enviarSolicitudAmistad(String de, String para) {
        // Verificar que no exista ya una solicitud pendiente
        List<SolicitudAmistad> listaPara = cargarSolicitudes(para);
        for (SolicitudAmistad s : listaPara) {
            if (s.getDeMiParte().equals(de) && s.isPendiente()) return false;
        }

        SolicitudAmistad nueva = new SolicitudAmistad(de, para);
        listaPara.add(nueva);
        guardarSolicitudes(para, listaPara);
        return true;
    }

    
     //Acepta una solicitud: añade ambos usuarios como amigos mutuamente
     // y actualiza el estado de la solicitud.
     
    public static void aceptarSolicitud(SolicitudAmistad solicitud) {
        solicitud.setEstado(SolicitudAmistad.Estado.ACEPTADA);

        // Guardar solicitud actualizada en el inbox del destinatario
        String paraUser = solicitud.getParaUsuario();
        List<SolicitudAmistad> lista = cargarSolicitudes(paraUser);
        reemplazarSolicitud(lista, solicitud);
        guardarSolicitudes(paraUser, lista);

        // Agregar amistad mutua en ambos Usuarios y guardar
        Usuarios userPara = GestorArchivos.cargarUsuario(paraUser);
        Usuarios userDe   = GestorArchivos.cargarUsuario(solicitud.getDeMiParte());
        if (userPara != null && userDe != null) {
            userPara.agregarAmigo(userDe.getUsername());
            userDe.agregarAmigo(userPara.getUsername());
            GestorArchivos.guardarUsuario(userPara);
            GestorArchivos.guardarUsuario(userDe);
        }
    }

    // Rechaza una solicitud y actualiza el estado. 
    public static void rechazarSolicitud(SolicitudAmistad solicitud) {
        solicitud.setEstado(SolicitudAmistad.Estado.RECHAZADA);
        String paraUser = solicitud.getParaUsuario();
        List<SolicitudAmistad> lista = cargarSolicitudes(paraUser);
        reemplazarSolicitud(lista, solicitud);
        guardarSolicitudes(paraUser, lista);
    }

   

    //Devuelve todos los retos almacenados para un usuario. 
    @SuppressWarnings("unchecked")
    public static List<Reto> cargarRetos(String username) {
        String ruta = RUTA_BASE + username + "/retos.bin";
        File f = new File(ruta);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Reto>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error cargando retos de " + username + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Reescribe la lista completa de retos de un usuario. 
    public static void guardarRetos(String username, List<Reto> lista) {
        GestorArchivos.crearCarpetaUsuario(username);
        String ruta = RUTA_BASE + username + "/retos.bin";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.out.println("Error guardando retos de " + username + ": " + e.getMessage());
        }
    }

    
      // Crea un nuevo reto y lo guarda en el inbox del retado.
      // También guarda una copia en el inbox del retador (para ver resultados).
     
    public static void enviarReto(Reto reto) {
        // Guardar en inbox del retado (para que lo vea y acepte/rechace)
        List<Reto> retosRetado = cargarRetos(reto.getUsernameRetado());
        retosRetado.add(reto);
        guardarRetos(reto.getUsernameRetado(), retosRetado);

        // Guardar en inbox del retador (para ver resultado posterior)
        List<Reto> retosRetador = cargarRetos(reto.getUsernameRetador());
        retosRetador.add(reto);
        guardarRetos(reto.getUsernameRetador(), retosRetador);
    }

    
     //Actualiza el reto en AMBOS inboxes después de que el retado jugó.
     // Debe llamarse con el Reto ya en estado FINALIZADO o RECHAZADO.
     
    public static void actualizarReto(Reto reto) {
        // Actualizar en inbox del retado
        List<Reto> retosRetado = cargarRetos(reto.getUsernameRetado());
        reemplazarReto(retosRetado, reto);
        guardarRetos(reto.getUsernameRetado(), retosRetado);

        // Actualizar en inbox del retador
        List<Reto> retosRetador = cargarRetos(reto.getUsernameRetador());
        reemplazarReto(retosRetador, reto);
        guardarRetos(reto.getUsernameRetador(), retosRetador);
    }
    // En lugar de reescribir retosRetado completo (simple alternativa)
public static void actualizarEstadoReto(String username, String idReto, int nuevoEstado) {
    String ruta = RUTA_BASE + username + "/retos.dat";
    try (RandomAccessFile raf = new RandomAccessFile(ruta, "rw")) {
        int tamRegistro = 128; // tamaño fijo por reto
        long total = raf.length() / tamRegistro;
        for (long i = 0; i < total; i++) {
            raf.seek(i * tamRegistro);
            byte[] idBytes = new byte[36]; 
            raf.read(idBytes);
            if (new String(idBytes).trim().equals(idReto)) {
                raf.seek(i * tamRegistro + 36); 
                raf.writeInt(nuevoEstado);
                return;
            }
        }
    } catch (IOException e) {}
}

  

    private static void reemplazarSolicitud(List<SolicitudAmistad> lista, SolicitudAmistad nueva) {
        for (int i = 0; i < lista.size(); i++) {
            SolicitudAmistad s = lista.get(i);
            if (s.getDeMiParte().equals(nueva.getDeMiParte())
             && s.getParaUsuario().equals(nueva.getParaUsuario())) {
                lista.set(i, nueva);
                return;
            }
        }
        lista.add(nueva); // Si no estaba, agregar
    }

    private static void reemplazarReto(List<Reto> lista, Reto nuevo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdReto().equals(nuevo.getIdReto())) {
                lista.set(i, nuevo);
                return;
            }
        }
        lista.add(nuevo); // Si no estaba, agregar
    }
}