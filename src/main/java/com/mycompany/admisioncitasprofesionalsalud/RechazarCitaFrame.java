package com.mycompany.admisioncitasprofesionalsalud;

import com.mycompany.admisioncitasprofesionalsalud.utils.SwingUtils;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class RechazarCitaFrame extends JInternalFrame {
    private final CitasFrame parentCitasFrame;
    private final int citaId;
    private JTextArea txtRazon;

    /**
     * Crea un JInternalFrame para ingresar el motivo de rechazo.
     * @param parent El CitasFrame padre para llamar a la función de procesamiento.
     * @param citaId El ID de la cita que se va a rechazar.
     */
    public RechazarCitaFrame(CitasFrame parent, int citaId) {
        // Título, redimensionable, cerrable, no maximizable, iconizable
        super("Motivo de Rechazo Cita #" + citaId, true, true, false, true); 
        this.parentCitasFrame = parent;
        this.citaId = citaId;

        initUI();
        pack();

        // **********************************************
        // 2. CENTRADO DEL FRAME
        JDesktopPane desktop = parent.getDesktopPane();
        if (desktop != null) {
            SwingUtils.centrarEnDesktop(this, desktop);
        } else {
            // Si no hay JDesktopPane (ej. durante pruebas), usar una ubicación fija
            setLocation(100, 100);
        }
        // **********************************************
        // Agregar este JInternalFrame al mismo JDesktopPane que el padre
        parent.getDesktopPane().add(this); 
        setVisible(true);
        txtRazon.requestFocusInWindow(); // Poner foco automáticamente
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // --- Área de Texto de la Razón ---
        txtRazon = new JTextArea(5, 35);
        txtRazon.setLineWrap(true);
        txtRazon.setWrapStyleWord(true);
        JScrollPane scrollRazon = new JScrollPane(txtRazon);
        
        // Estilo solicitado: caja de texto multilínea con borde titulado
        scrollRazon.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Razón de Rechazo (Opcional)"
        , TitledBorder.LEFT, TitledBorder.TOP));
        
        mainPanel.add(scrollRazon, BorderLayout.CENTER);

        // --- Panel de Botones ---
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // El texto del botón solicitado por el usuario
        JButton btnAceptar = new JButton("Confirmar"); 
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> aceptarRechazo());
        btnCancelar.addActionListener(e -> dispose());
        
        botonPanel.add(btnAceptar);
        botonPanel.add(btnCancelar);

        mainPanel.add(botonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void aceptarRechazo() {
        String razon = txtRazon.getText().trim();
        // Llamar al método de procesamiento en el frame padre (CitasFrame)
        parentCitasFrame.aprobarCitaDesdeTabla(false, parentCitasFrame.getToken(), razon);
        dispose(); // Cerrar este frame después de la acción
    }
}