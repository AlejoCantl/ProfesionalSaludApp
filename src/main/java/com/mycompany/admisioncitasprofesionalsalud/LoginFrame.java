package com.mycompany.admisioncitasprofesionalsalud;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder; // Importar para añadir espacio alrededor

public class LoginFrame extends JInternalFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private ProfesionalSaludApp parent;
    private Callback callback;

    public interface Callback {
        void onLogin(String token, int usuarioId);
    }

    public LoginFrame(ProfesionalSaludApp parent) {
        // Título, redimensionable, cerrable, maximizable, iconifiable
        super("Iniciar Sesión", false, true, false, false); 
        this.parent = parent;
        
        // No establecer setSize/setLayout aquí. Usaremos pack() después de initUI
        setResizable(true); 
        
        // Llamamos a un initUI modificado
        initUI();
        
        // 1. Empacar los componentes para que la ventana tome el tamaño mínimo necesario
        pack(); 
    }

    private void initUI() {
        // --- 1. Crear el Panel del Formulario (Contenido) ---
        // Usaremos GridLayout o SpringLayout para el formulario en sí.
        // GridLayout es simple y está bien, pero NO debe ir en el JInternalFrame principal.
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        // Agregar un borde vacío para darle un poco de "aire"
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Fila 1: Usuario
        formPanel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField(15); // Darle un tamaño preferido (opcional)
        formPanel.add(txtUsuario);
        
        // Fila 2: Contraseña
        formPanel.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField(15); // Darle un tamaño preferido (opcional)
        formPanel.add(txtContrasena);

        // Fila 3: Botón
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.addActionListener(e -> realizarLogin());
        
        // Usamos un JPanel para centrar el botón en su celda (columna 2)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnLogin);

        formPanel.add(new JLabel()); // Celda vacía (Columna 1, Fila 3)
        formPanel.add(buttonPanel);  // El botón (Columna 2, Fila 3)

        // --- 2. Crear el Panel Contenedor Central ---
        // Usamos GridBagLayout en este wrapper para centrar el formPanel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        
        // Añadir el panel del formulario al wrapper. 
        // Las restricciones por defecto lo centran.
        centerWrapper.add(formPanel, new GridBagConstraints());

        // --- 3. Añadir el Wrapper al JInternalFrame ---
        // Establecer el Layout del JInternalFrame a BorderLayout
        setLayout(new BorderLayout());
        
        // Añadir el panel wrapper al centro. Este panel se estira, 
        // pero el GridBagLayout mantiene el formPanel centrado.
        add(centerWrapper, BorderLayout.CENTER);
    }

    // ... (El resto de los métodos realizarLogin y setCallback permanecen iguales) ...
    private void realizarLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        try {
            String token = ApiService.login(usuario, contrasena);
            int id = ApiService.getUsuarioId(token);
            callback.onLogin(token, id);
            JOptionPane.showMessageDialog(this, "Login exitoso");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}