package com.mycompany.admisioncitasprofesionalsalud;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JInternalFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private ProfesionalSaludApp parent;
    private Callback callback;

    public interface Callback {
        void onLogin(String token, int usuarioId);
    }

    public LoginFrame(ProfesionalSaludApp parent) {
        super("Iniciar Sesión", false, true, false, false);
        this.parent = parent;
        setResizable(false);
        initUI();
        pack();
    }

    private void initUI() {
        // --- Dimensiones ajustadas ---
        Dimension inputButtonSize = new Dimension(150, 28); 

        // --- 1. Crear el Panel del Formulario (Contenido) con GridBagLayout ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Espacio (padding)
        gbc.anchor = GridBagConstraints.WEST; // Etiquetas alineadas a la izquierda
        
        // Inicializar Inputs
        txtUsuario = new JTextField(15);
        txtContrasena = new JPasswordField(15);
        
        // Aplicar altura ajustada y bordes redondeados (FlatLaf)
        txtUsuario.setPreferredSize(inputButtonSize);
        txtContrasena.setPreferredSize(inputButtonSize);
        txtUsuario.putClientProperty("JComponent.roundRect", true);
        txtContrasena.putClientProperty("JComponent.roundRect", true);
        
        
        // Fila 1: Usuario
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.weightx = 0; 
        formPanel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1; // Columna 1
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        formPanel.add(txtUsuario, gbc);

        // Fila 2: Contraseña
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 1; // Fila 1
        gbc.fill = GridBagConstraints.NONE; 
        formPanel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1; // Columna 1
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        formPanel.add(txtContrasena, gbc);

        // Fila 3: Botón
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.addActionListener(e -> realizarLogin());

        // Aplicar altura ajustada y bordes redondeados al botón
        btnLogin.setPreferredSize(inputButtonSize); 
        
        btnLogin.putClientProperty("JComponent.roundRect", true);
        btnLogin.putClientProperty("FlatLaf.buttonArc", 20); 
        
        // --- CENTRADO DEL BOTÓN ---
        gbc.gridx = 0; // Iniciar en la columna 0
        gbc.gridy = 2; // Fila 2
        gbc.gridwidth = 2; // Ocupar ambas columnas (0 y 1)
        gbc.anchor = GridBagConstraints.CENTER; // Anclar al centro de su espacio
        gbc.fill = GridBagConstraints.NONE; 
        
        // Agregar espacio antes del botón
        gbc.insets = new Insets(20, 5, 5, 5); 
        
        formPanel.add(btnLogin, gbc);
        
        // --- 2. Crear el Panel Contenedor Central para centrar el formulario ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(formPanel, new GridBagConstraints());

        // --- 3. Añadir el Wrapper al JInternalFrame ---
        setLayout(new BorderLayout());
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        
        try {
            // 1. Intentar iniciar sesión (obtener token)
            String token = ApiService.login(usuario, contrasena);
            
            // 2. Obtener el ID del rol a partir del token (asumiendo que ApiService tiene este nuevo método)
            int rolId = ApiService.getRolId(token);
            
            // 3. Validar el rol
            final int ROL_PERMITIDO = 3;
            if (rolId != ROL_PERMITIDO) {
                // Si el rol no es 3, lanzamos una excepción de seguridad/autorización
                throw new Exception("Rol no autorizado. Solo se permite el rol Profesional de salud.");
            }
            
            // 4. Si el rol es correcto, obtener el ID de usuario y proceder
            int id = ApiService.getUsuarioId(token);
            
            callback.onLogin(token, id);
            JOptionPane.showMessageDialog(this, "Login exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error de credenciales o autorización: " + ex.getMessage(), 
                "Error de Login", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}