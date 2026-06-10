/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author user
 */
public class GestorArchivos {
    private static final String RUTA_BASE = "usuarios/";
    
    public static void crearCarpetaUsuario (String username){
        File carpeta = new File (RUTA_BASE + username);
        if (!carpeta.exists()){
            carpeta.mkdirs();
        }
    }
    
    
    public static void guardarUsuario (Usuarios usuario){
        crearCarpetaUsuario(usuario.getUsername());
        String ruta= RUTA_BASE + usuario.getUsername() + "/datos.bin";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))){
            oos.writeObject(usuario);
        }catch (IOException e){
            System.out.println("Error al guardar usuario: " +e.getMessage());
        }
    
    
}
    
public static Usuarios cargarUsuario (String username){
    String ruta = RUTA_BASE + username + "/datos.bin";
    try (ObjectInputStream ois = new ObjectInputStream (new FileInputStream(ruta))){
        return (Usuarios) ois.readObject();
    }catch(IOException | ClassNotFoundException e){
        System.out.println("Usario no encontrado: " +e.getMessage());
        return null;
    }
}    
    
public static boolean usuarioExiste (String username){
    File archivo = new File (RUTA_BASE + username + "/datos.bin");
    return archivo.exists();
}    
 
public static String hashContrasena (String contrasena){
    try{
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       byte[] hash = md.digest(contrasena.getBytes());
      String resultado= "";
      for (byte b : hash){
          resultado += String.format("%02x", b);
      }
      return resultado;
    } catch (NoSuchAlgorithmException e){
        return contrasena;
    }
}


}

