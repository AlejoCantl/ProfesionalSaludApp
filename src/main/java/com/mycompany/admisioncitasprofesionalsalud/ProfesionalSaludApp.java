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
        setSize(1000, 700); // Más grande para MDI        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        desktopPane = new JDesktopPane();
        add(desktopPane);

        crearMenu();
        mostrarLogin();
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu moduloMenu = new JMenu("Módulos");
        JMenuItem perfilItem = new JMenuItem("Perfil");
        JMenuItem citasItem = new JMenuItem("Citas");

        perfilItem.addActionListener(e -> new PerfilFrame(this, accessToken).setVisible(true));
        citasItem.addActionListener(e -> new CitasFrame(this, accessToken).setVisible(true));

        moduloMenu.add(perfilItem);
        moduloMenu.add(citasItem);
        menuBar.add(moduloMenu);
        setJMenuBar(menuBar);
    }

    private void mostrarLogin() {
        LoginFrame login = new LoginFrame(this);
        login.setCallback((token, id) -> {
            this.accessToken = token;
            this.usuarioId = id;
        });
        agregarFrame(login);
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