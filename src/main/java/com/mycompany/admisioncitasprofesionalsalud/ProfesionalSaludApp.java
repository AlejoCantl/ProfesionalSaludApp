package com.mycompany.admisioncitasprofesionalsalud;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.admisioncitasprofesionalsalud.utils.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class ProfesionalSaludApp extends JFrame {
    private final JDesktopPane desktopPane;
    private String accessToken;
    private int usuarioId;
    
    // Referencias a los items de menú para control de estado
    private JMenuItem perfilItem;
    private JMenuItem citasItem;
    private JMenuItem cerrarSesionItem;
    private JMenuItem iniciarSesionItem; // Item para reabrir el Login
    private JMenuItem salirItem;

    public ProfesionalSaludApp() {
        setTitle("Profesional de Salud - Fundación Amigos de los Niños");
        setSize(1000, 700); // Más grande para MDI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Usar BorderLayout para el JFrame
        setLayout(new BorderLayout()); 
        desktopPane = new JDesktopPane();
        add(desktopPane, BorderLayout.CENTER);
        
        crearMenu();
        // El menú se crea DESHABILITADO hasta que el usuario inicie sesión
        actualizarEstadoMenu(false); 
        
        mostrarLogin();
    }
    
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // --- 1. MENÚ PRINCIPAL: VISTAS/NAVEGACIÓN ---
        JMenu vistasMenu = new JMenu("Vistas");
        
        perfilItem = new JMenuItem("Mi Perfil");
        citasItem = new JMenuItem("Citas Pendientes");
        
        perfilItem.addActionListener(e -> abrirFrameUnico(new PerfilFrame(this, accessToken), "Perfil del Profesional"));
        citasItem.addActionListener(e -> abrirFrameUnico(new CitasFrame(this, accessToken), "Citas Pendientes"));
        
        vistasMenu.add(citasItem); // La vista principal va primero
        vistasMenu.add(perfilItem);
        
        // --- 2. MENÚ SECUNDARIO: SESIÓN/ARCHIVO ---
        JMenu sesionMenu = new JMenu("Sesión");
        
        iniciarSesionItem = new JMenuItem("Iniciar Sesión");
        cerrarSesionItem = new JMenuItem("Cerrar Sesión");
        salirItem = new JMenuItem("Salir de la Aplicación");
        
        iniciarSesionItem.addActionListener(e -> mostrarLogin()); 
        cerrarSesionItem.addActionListener(e -> cerrarSesion());
        salirItem.addActionListener(e -> System.exit(0)); 
        
        sesionMenu.add(iniciarSesionItem);
        sesionMenu.add(cerrarSesionItem);
        sesionMenu.addSeparator(); 
        sesionMenu.add(salirItem);

        // AÑADIR MENÚS A LA BARRA
        menuBar.add(vistasMenu);
        menuBar.add(sesionMenu);
        
        setJMenuBar(menuBar);
    }
    
    // Nuevo método para habilitar/deshabilitar opciones de menú
    private void actualizarEstadoMenu(boolean logueado) {
        if (perfilItem != null) {
            // Vistas: Habilitadas SOLO si está logueado
            perfilItem.setEnabled(logueado);
            citasItem.setEnabled(logueado);
            
            // Sesión:
            cerrarSesionItem.setEnabled(logueado);         // Habilitado si logueado (para cerrar)
            iniciarSesionItem.setEnabled(!logueado);       // Habilitado si NO logueado (para abrir)
            salirItem.setEnabled(true);                    // Siempre habilitado
        }
    }
    
    private void cerrarSesion() {
        // 1. Limpiar datos de sesión
        this.accessToken = null;
        this.usuarioId = 0;
        
        // 2. Cerrar todas las ventanas internas
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
        
        // 3. Deshabilitar menú de vistas y habilitar Iniciar Sesión
        actualizarEstadoMenu(false);
        
        // 4. Mostrar la ventana de login
        mostrarLogin();
    }
    
    private void mostrarLogin() {
        // Evita abrir múltiples ventanas de Login
        for (JInternalFrame existingFrame : desktopPane.getAllFrames()) {
            if (existingFrame instanceof LoginFrame) {
                try {
                    existingFrame.setIcon(false);
                    existingFrame.setSelected(true);
                } catch (java.beans.PropertyVetoException ex) {}
                return;
            }
        }
        
        LoginFrame login = new LoginFrame(this);
        login.setCallback((token, id) -> {
            this.accessToken = token;
            this.usuarioId = id;
            
            // Habilitar menú después de login exitoso
            actualizarEstadoMenu(true);
            
            // Abrir automáticamente CitasFrame
            abrirFrameUnico(new CitasFrame(this, accessToken), "Citas Pendientes");
        });
        agregarFrame(login);
    }
    
    // Método nuevo: Verifica si el frame ya está abierto; si no, lo abre
    private void abrirFrameUnico(JInternalFrame frame, String titulo) {
        for (JInternalFrame existingFrame : desktopPane.getAllFrames()) {
            if (existingFrame.getTitle().equals(titulo)) {
                try {
                    existingFrame.setIcon(false); 
                    existingFrame.setSelected(true); 
                } catch (java.beans.PropertyVetoException ex) {
                    ex.printStackTrace();
                }
                return; 
            }
        }
        agregarFrame(frame);
    }
    
    public void agregarFrame(JInternalFrame frame) {
        desktopPane.add(frame);
        // Forzar tamaño del desktop antes de centrar
        if (desktopPane.getWidth() == 0 || desktopPane.getHeight() == 0) {
            desktopPane.setSize(getContentPane().getSize());
        }
        // Centrar DESPUÉS de agregar y pack
        SwingUtilities.invokeLater(() -> {
            SwingUtils.centrarEnDesktop(frame, desktopPane);
            frame.setVisible(true);
            try {
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}
        });
    }
    
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public int getUsuarioId() {
        return usuarioId;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // APLICAR LOOK AND FEEL PRIMERO
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ex) {
                System.err.println("FlatLaf falló: " + ex.getMessage());
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // AHORA CREAR LA VENTANA
            ProfesionalSaludApp app = new ProfesionalSaludApp();
            app.setVisible(true); 
        });
    }
}