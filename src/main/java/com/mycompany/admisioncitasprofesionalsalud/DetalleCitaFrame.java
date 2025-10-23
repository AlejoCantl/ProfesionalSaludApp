package com.mycompany.admisioncitasprofesionalsalud;

import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;

public class DetalleCitaFrame extends JInternalFrame {

    private final ProfesionalSaludApp parent;
    private final String token;
    private final int citaId;

    public DetalleCitaFrame(ProfesionalSaludApp parent, String token, int citaId) {
        super("Detalle Cita #" + citaId, true, true, true, true);
        this.parent = parent;
        this.token = token;
        this.citaId = citaId;
        cargarDetalle();
    }

    private void cargarDetalle() {
        try {
            JSONObject detalle = ApiService.getDetalleCita(token, citaId);

            // Panel principal que contendrá todos los subpaneles
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margen exterior

            // 1. DATOS DEL PACIENTE
            JPanel pacientePanel = crearPanelAgrupado("Información del Paciente");
            pacientePanel.add(label("<b>Nombre:</b> " + detalle.getString("nombre") + " " + detalle.getString("apellido")));
            pacientePanel.add(label("<b>Identificación:</b> " + detalle.getString("identificacion")));
            pacientePanel.add(label("Tipo: " + detalle.getString("tipo_paciente")));
            pacientePanel.add(label("Correo: " + detalle.getString("correo")));

            // 2. DATOS CLÍNICOS RELEVANTES
            JPanel clinicoPanel = crearPanelAgrupado("Datos Clínicos");
            
            clinicoPanel.add(label("<b>Peso:</b> " + detalle.getDouble("peso") + " kg"));
            clinicoPanel.add(label("<b>Talla:</b> " + detalle.getDouble("talla") + " cm"));
            
            clinicoPanel.add(Box.createVerticalStrut(5)); // Espacio antes del subtítulo
            
            // --- MODIFICACIÓN CLAVE: Subtítulo para Enfermedades ---
            // El título "Enfermedades" debe estar alineado a la izquierda.
            JLabel lblEnfermedades = new JLabel("<html><b>Enfermedades:</b></html>");
            lblEnfermedades.setAlignmentX(Component.LEFT_ALIGNMENT); // Asegurar alineación
            clinicoPanel.add(lblEnfermedades); 
            
            // La JTextArea para el texto descriptivo
            JTextArea txtEnfermedades = new JTextArea(detalle.getString("enfermedades"));
            txtEnfermedades.setEditable(false);
            txtEnfermedades.setLineWrap(true);
            txtEnfermedades.setWrapStyleWord(true);
            txtEnfermedades.setCaretPosition(0);
            txtEnfermedades.setCaretColor(new Color(0,0,0,0));
            
            // Se usa un JScrollPane para manejar texto largo
            JScrollPane scrollEnfermedades = new JScrollPane(txtEnfermedades, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );
            
            // Estilos para que la caja de texto no ocupe un espacio gigante, pero sea legible
            scrollEnfermedades.setPreferredSize(new Dimension(300, 60)); 
            scrollEnfermedades.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            scrollEnfermedades.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            clinicoPanel.add(scrollEnfermedades);
            // --- FIN MODIFICACIÓN CLAVE ---


            // 3. DATOS DE LA CITA
            JPanel citaPanel = crearPanelAgrupado("Detalles de la Cita");
            citaPanel.add(label("<b>Médico:</b> " + detalle.getString("medico")));
            citaPanel.add(label("<b>Especialidad:</b> " + detalle.getString("especialidad")));
            citaPanel.add(label("<b>Fecha y Hora:</b> " + detalle.getString("fecha") + " " + detalle.getString("hora")));

            // AÑADIR TODOS LOS PANELES AL PANEL PRINCIPAL
            mainPanel.add(pacientePanel);
            mainPanel.add(Box.createVerticalStrut(10)); // Separador
            mainPanel.add(clinicoPanel);
            mainPanel.add(Box.createVerticalStrut(10)); // Separador
            mainPanel.add(citaPanel);
            mainPanel.add(Box.createVerticalStrut(15)); // Separador antes de la acción

            // 4. SECCIÓN DE APROBACIÓN/RECHAZO (RAZÓN Y BOTONES)
            JTextArea txtRazon = new JTextArea("Razón del rechazo (opcional)", 3, 30);
            txtRazon.setLineWrap(true);
            txtRazon.setWrapStyleWord(true);
            txtRazon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Razón de Rechazo"),
                new EmptyBorder(5, 5, 5, 5)
            ));

            JButton btnAprobar = new JButton("✅ Aprobar Cita");
            JButton btnRechazar = new JButton("❌ Rechazar Cita");

            btnAprobar.addActionListener(e -> aprobar(true, null));
            btnRechazar.addActionListener(e -> aprobar(false, txtRazon.getText()));

            // Se cambia FlowLayout.RIGHT por FlowLayout.CENTER o se deja FlowLayout.LEFT 
            // si se quiere alineación estricta, pero FlowLayout.RIGHT es común para botones de acción.
            // Para mantener la consistencia con un flujo de trabajo típico (acción a la derecha):
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botones.add(btnAprobar);
            botones.add(btnRechazar);
            botones.setAlignmentX(Component.LEFT_ALIGNMENT); // Asegurar que el panel de botones se alinee a la izquierda

            // Añadir componentes de acción
            mainPanel.add(new JScrollPane(txtRazon));
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(botones);


            // -------------------------------------------------
            add(mainPanel);
            pack();
            setMinimumSize(getSize());
            parent.agregarFrame(this);
            // -------------------------------------------------

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el detalle de la cita:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    // Método helper para crear paneles agrupados consistentemente
    private JPanel crearPanelAgrupado(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Asegurar que el panel se alinee a la izquierda
        return panel;
    }


    private void aprobar(boolean aprobado, String razon) {
        try {
            if (!aprobado && (razon == null || razon.trim().isEmpty()
                    || razon.equals("Razón del rechazo (opcional)"))) {
                razon = "";
            }
            ApiService.aprobarCita(token, citaId, aprobado, razon);
            JOptionPane.showMessageDialog(this,
                    aprobado ? "Cita aprobada."
                            : "Cita rechazada.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            // Opcional: recargar la tabla de citas
            for(JInternalFrame frame : parent.getDesktopPane().getAllFrames() ){
               if(frame instanceof CitasFrame){
               ((CitasFrame) frame).cargarCitas(token);
               }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar la cita:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel label(String html) {
        // ASEGURAMOS ALINEACIÓN A LA IZQUIERDA PARA TODOS LOS LABELS
        JLabel lbl = new JLabel("<html>" + html + "</html>");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}