package com.badlogic.drop.archivos;

import com.badlogic.drop.config.Usuarios;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

public class GestorArchivos {
    private static final String RUTA_BASE = "usuarios/";
    
    public static void crearCarpetaUsuario (String username){
        File carpeta = new File (RUTA_BASE + username);
        if (!carpeta.exists()){
            carpeta.mkdirs();
        }
    }
    
    private static String getRutaBase(){
        String ruta = System.getProperty("user.dir" + "/usuarios/");
        File carpeta = new File (ruta);
        if (!carpeta.exists()){
            carpeta.mkdirs();
        }
        return ruta;
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
public static long buscarOffsetUsuario(String username) {
    try (RandomAccessFile raf = new RandomAccessFile("usuarios/indice.dat", "r")) {
        long totalRegistros = raf.length() / 40; 
        for (int i = 0; i < totalRegistros; i++) {
            raf.seek(i * 40L);
            byte[] nameBytes = new byte[32];
            raf.read(nameBytes);
            String nombre = new String(nameBytes).trim();
            if (nombre.equals(username)) {
                return raf.readLong();
            }
        }
    } catch (IOException e){}
    return -1;
}

public static Usuarios[] cargarTodosLosUsuarios(){ // todos los usuarios (rankig, estadisticas globales)
 File carpeta = new File (RUTA_BASE);
 if (!carpeta.exists() || !carpeta.isDirectory()){
     return new Usuarios[0];
 }
 
 File[] carpetas = carpeta.listFiles();
 if (carpetas == null || carpetas.length == 0)
     return new Usuarios[0];
 
 int count = 0;
 for (File f : carpetas){
     if (f.isDirectory()) count++;
 }
 
 Usuarios[] lista = new Usuarios[count];
 int index = 0;
 for (File f: carpetas){
     if (f.isDirectory()){
         try{
           lista[index] = cargarUsuario(f.getName());  
         }catch (Exception e){
             System.out.println("Error al cargar usuario" +f.getName());
             lista[index] = null;
             
         }
         index++;
     }
 }
 return lista;
}


public static String guardarAvatarPersonalizado (String username, String rutaOrigen){
    try{
        File origen = new File (rutaOrigen);
        if (!origen.exists())
            return null;
        
        String nombre = origen.getName();
        String extension = nombre.substring(nombre.lastIndexOf(".")).toLowerCase();
        
        if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg"))
            return null;
   
        
    crearCarpetaUsuario(username);
    String destino = RUTA_BASE + username + "/avatar" + extension;
    File archivoDestino = new File (destino);
    
    Files.copy(
            origen.toPath(),
            archivoDestino.toPath(),
            StandardCopyOption.REPLACE_EXISTING);
    
       return destino;
    
    }catch (IOException e){
        System.out.println("Error al guardar avatar: " +e.getMessage());
        return null;
    }
    
}

public static String getRutaAvatar(String username) {
    String[] extensiones = {".png", ".jpg", ".jpeg"};
    for (String ext : extensiones) {
        File f = new File(RUTA_BASE + username + "/avatar" + ext);
        if (f.exists()) return f.getAbsolutePath();
    }
    return null;
}

}
