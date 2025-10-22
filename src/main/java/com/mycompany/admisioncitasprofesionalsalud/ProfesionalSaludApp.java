package com.mycompany.admisioncitasprofesionalsalud;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.admisioncitasprofesionalsalud.utils.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class ProfesionalSaludApp extends JFrame {
    private JDesktopPane desktopPane;
    private String accessToken;
    private int usuarioId;
    public ProfesionalSaludApp() {
        setTitle("Profesional de Salud - Fundación Amigos de los Niños");
        setSize(1000, 700); // Más grande para MDI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        desktopPane = new JDesktopPane();
        add(desktopPane);
        crearMenu();
        mostrarLogin();
    }
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenu vistasSubMenu = new JMenu("Vistas");
        JMenuItem perfilItem = new JMenuItem("Perfil");
        JMenuItem citasItem = new JMenuItem("Citas");
        JMenuItem cerrarItem = new JMenuItem("Cerrar");
        
        perfilItem.addActionListener(e -> abrirFrameUnico(new PerfilFrame(this, accessToken), "Perfil del Profesional"));
        citasItem.addActionListener(e -> abrirFrameUnico(new CitasFrame(this, accessToken), "Citas Pendientes"));
        cerrarItem.addActionListener(e -> System.exit(0)); // Cerrar la aplicación
        
        vistasSubMenu.add(perfilItem);
        vistasSubMenu.add(citasItem);
        archivoMenu.add(vistasSubMenu);
        archivoMenu.addSeparator(); // Separador visual
        archivoMenu.add(cerrarItem);
        
        menuBar.add(archivoMenu);
        setJMenuBar(menuBar);
    }
    private void mostrarLogin() {
        LoginFrame login = new LoginFrame(this);
        login.setCallback((token, id) -> {
            this.accessToken = token;
            this.usuarioId = id;
            // Abrir automáticamente CitasFrame después del login
            abrirFrameUnico(new CitasFrame(this, accessToken), "Citas Pendientes");
        });
        agregarFrame(login);
    }
    // Método nuevo: Verifica si el frame ya está abierto; si no, lo abre
    private void abrirFrameUnico(JInternalFrame frame, String titulo) {
        for (JInternalFrame existingFrame : desktopPane.getAllFrames()) {
            if (existingFrame.getTitle().equals(titulo)) {
                try {
                    existingFrame.setIcon(false); // Desiconificar si está minimizado
                    existingFrame.setSelected(true); // Seleccionar y traer al frente
                } catch (java.beans.PropertyVetoException ex) {
                    ex.printStackTrace();
                }
                return; // No abrir uno nuevo
            }
        }
        // Si no existe, agregar el nuevo
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
            app.setVisible(true); // Esto ya está bien
        });
    }
}