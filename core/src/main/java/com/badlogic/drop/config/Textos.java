package com.badlogic.drop.config;

//Sistema de idiomas (Español / English) 
public class Textos {

    private static boolean ingles = false;

    public static void setIngles(boolean si) { ingles = si; }
    public static boolean isIngles() { return ingles; }
    public static void toggle() { ingles = !ingles; }

    private static String t(String es, String en) { return ingles ? en : es; }

    // ─── LoginScreen ───
    public static String TITULO_JUEGO(){ 
        return "Flow Free Game"; 
    }
    public static String USUARIO(){ 
        return t("Usuario", "Username"); 
    }
    public static String CONTRASENA(){ 
        return t("Contrasena", "Password"); 
    }
    public static String CONTRASENA_MIN(){ 
        return t("Contrasena (mín. 6 caracteres)", "Password (min. 6 characters)"); 
    }
    public static String CONFIRMAR(){ 
        return t("Confirmar contrasena", "Confirm password"); 
    }
    public static String INICIAR_SESION(){ 
        return t("Iniciar sesión", "Log in"); 
    }
    public static String NO_TIENES_CUENTA(){ 
        return t("¿No tienes cuenta? Regístrate", "Don't have an account? Sign up"); 
    }
    public static String CREAR_CUENTA(){ 
        return t("Crear cuenta", "Create account"); 
    }
    public static String YA_TENGO_CUENTA(){ 
        return t("Ya tengo cuenta", "I already have an account"); 
    }
    public static String NOMBRE_COMPLETO(){ 
        return t("Nombre completo", "Full name"); 
    }
    public static String MOSTRAR_PASS(){ 
        return t(" Mostrar contrasena", " Show password"); 
    }
    public static String MOSTRAR_PASSES(){ 
        return t(" Mostrar contrasenas", " Show passwords"); 
    }
    public static String ERROR_CAMPOS(){ 
        return t("Completa todos los campos.", "Fill all fields."); 
    }
    public static String ERROR_NO_ENCONTRADO(){ 
        return t("Usuario no encontrado.", "User not found."); 
    }
    public static String ERROR_PASS_INC(){ 
        return t("Contrasena incorrecta.", "Incorrect password."); 
    }
    public static String ERROR_PASS_LEN() { 
        return t("La contrasena debe tener al menos 6 caracteres.", "Password must be at least 6 characters."); 
    }
    public static String ERROR_PASS_MATCH(){ 
        return t("Las contrasenas no coinciden.", "Passwords don't match."); 
    }
    public static String ERROR_USER_EXISTS(){ 
        return t("Ese nombre de usuario ya existe.", "That username already exists."); 
    }

    // ─── MenuScreen ───
    public static String BIENVENIDO(){ 
        return t("Bienvenido, ", "Welcome, "); 
    }
    public static String PARTIDAS(){ 
        return t("Partidas jugadas: ", "Games played: "); 
    }
    public static String NIVELES_COMP(){ 
        return t("Niveles completados: ", "Levels completed: "); 
    }
    public static String RANKING(){ 
        return t("Puntuaje: ", "Score: ");  //***************
    }
    public static String JUGAR(){ 
        return t("Jugar", "Play"); 
    }
    public static String ESTADISTICAS(){ 
        return t("Estadísticas", "Stats"); 
    }
    public static String CERRAR_SESION(){ 
        return t("Cerrar sesion", "Log out"); 
    }
    public static String VER_PERFIL(){ 
        return t("Ver perfil de otro jugador", "View other player's profile"); 
    }
    public static String RANKING_BTN(){ 
        return t("Ranking global", "Global ranking"); 
    }
    public static String PERFIL_AVATAR(){ 
        return t("Avatar y nombre de usuario", "Avatar & username"); 
    }
    public static String IDIOMA(){ return t("Idioma: Espanol", "Language: English"); 
    }
    public static String INBOX(){ 
        return t("Bandeja de entrada", "Inbox"); 
    }

    // ─── MapaScreen ───
    public static String MAPA_NIVELES(){ 
        return t("Mapa de Niveles", "Level Map");
    }
    public static String COMPLETA_NIVELES(){ 
        return t("Completa los niveles en orden para desbloquear los siguientes", "Complete levels in order to unlock the next ones"); 
    }
    public static String VOLVER_MENU(){ 
        return t("← Volver al menu", "← Back to menu");
    }

    // ─── EstadísticasScreen ───
    public static String TITULO_STATS(){ return t("Estadisticas", "Statistics"); 
    }
    public static String TIEMPO_TOTAL(){ 
        return t("Tiempo total jugado: ", "Total play time: "); 
    }
    public static String MEJORES_TIEMPOS(){ 
        return t("Mejor tiempo por nivel:", "Best time per level:"); 
    }
    public static String SIN_REGISTROS(){ return t("  Sin registros aun.", "  No records yet."); 
    }
    public static String VECES_PERDIDAS() { return t ("Veces perdidas: ", "lost times: "); 
    }

    // ─── FirstScreen (HUD) ───
    public static String NIVEL(){
        return t("Nivel: ", "Level: "); 
    }
    public static String TIEMPO(){ 
        return t("Tiempo: ", "Time: "); 
    }
    public static String INTENTOS(){ 
        return t("  Intentos: ", "  Attempts: "); 
    }
    public static String DE(){ 
        return t(" de ", " of "); 
    }
    public static String REINICIAR(){ 
        return t("  [R] Reiniciar", "  [R] Restart");
    }
    public static String MENU(){ 
        return t("  [M] Menu", "  [M] Menu"); 
    }
    public static String GANASTE(){ 
        return t("¡GANASTE!", "YOU WIN!");
    }
    public static String PERDISTE(){ 
        return t("¡PERDISTE!", "YOU LOSE!"); 
    }
    public static String JUEGO_COMPLETADO(){ 
        return t("¡JUEGO COMPLETADO!", "GAME COMPLETED!"); 
    }
    public static String PUNTAJE(){
        return t("Puntaje: ", "Score: "); 
    }
    public static String PTS(){ 
        return "pts"; 
    }
    public static String MEJOR_PUNTAJE(){ 
        return t("Mejor puntaje: ", "Best score: "); 
    }
    public static String SIGUIENTE_EN(){ 
        return t("Siguiente nivel en ", "Next level in "); 
    }
    public static String VOLVER_MENU_MSG(){ 
        return t("Presiona M para volver al menú", "Press M to return to menu");
    }
    public static String REINTENTAR_MSG(){ 
        return t("Presiona R para reintentar", "Press R to retry");
    }

    // ─── AvatarScreen / perfil ───
    public static String SUBIR_AVATAR(){ 
        return t("Subir avatar personalizado", "Upload custom avatar"); 
    }
    public static String NUEVO_USER(){ 
        return t("Nuevo nombre de usuario", "New username"); 
    }
    public static String CONFIRMA_PASS(){ 
        return t("Confirma tu contrasena actual", "Confirm your current password"); 
    }
    public static String GUARDAR(){ 
        return t("Guardar cambios", "Save changes"); 
    }
    public static String CANCELAR(){ 
        return t("Cancelar", "Cancel"); 
    }
    public static String PASS_INCORRECTA(){ 
        return t("Contrasena incorrecta.", "Incorrect password."); 
    }
    public static String CAMBIOS_GUARDADOS(){ 
        return t("Cambios guardados correctamente.", "Changes saved successfully."); 
    }
    public static String AVATAR_ACTUAL(){ 
        return t("Avatar actual: ", "Current avatar: "); 
    }
    public static String SELECCIONAR_AVATAR(){
        return t("Seleccionar avatar", "Select avatar"); 
    }

    // ─── Buscar jugador ───
    public static String BUSCAR_JUGADOR(){ 
        return t("Buscar jugador", "Search player"); 
    }
    public static String PERFIL_DE(){
        return t("Perfil de ", "Profile of "); 
    }
    public static String NO_ENCONTRADO(){ 
        return t("Jugador no encontrado", "Player not found"); 
    }
    public static String INTRODUCE_USER(){ 
            return t("Introduce nombre de usuario", "Enter username"); 
    }

    // ─── Eliminar cuenta ───
    public static String ELIMINAR_CUENTA(){
        return t("Eliminar cuenta", "Delete account");
    }
    public static String CONFIRMAR_ELIMINAR(){
        return t("¿Estás seguro? Ingresa tu contraseña para eliminar la cuenta.",
                 "Are you sure? Enter your password to delete the account.");
    }
    public static String SI_ELIMINAR(){
        return t("Sí, eliminar mi cuenta", "Yes, delete my account");
    }
    public static String CUENTA_ELIMINADA(){
        return t("Cuenta eliminada.", "Account deleted.");
    }
    public static String ERROR_ELIMINAR(){
        return t("Error al eliminar la cuenta.", "Error deleting account.");
    }

    // ─── Amigos ───
    public static String VER_AMIGOS(){
        return t("Ver amigos", "Friends");
    }
    public static String AMIGOS_TITULO(){
        return t("Mis Amigos", "My Friends");
    }
    public static String SIN_AMIGOS(){
        return t("No tienes amigos aún.", "You have no friends yet.");
    }
    public static String RETAR(){
        return t("Retar", "Challenge");
    }
}
