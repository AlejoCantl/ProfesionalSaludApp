package com.mycompany.admisioncitasprofesionalsalud;

import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;

public class DetalleCitaFrame extends JInternalFrame {

    private final ProfesionalSaludApp parent;
    private final String token;
    private final int citaId;

    public DetalleCitaFrame(ProfesionalSaludApp parent, String token, int citaId) {
        super("Detalle Cita #" + citaId, true, true, true, true);
        this.parent = parent;
        this.token = token;
        this.citaId = citaId;

        // NO llamamos a setSize aquí → lo hará pack()
        cargarDetalle();
    }

    private void cargarDetalle() {
        try {
            JSONObject detalle = ApiService.getDetalleCita(token, citaId);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            panel.add(label("<b>Paciente:</b> " + detalle.getString("nombre") + " " + detalle.getString("apellido")));
            panel.add(label("Identificación:" + detalle.getString("identificacion")));
            panel.add(label("Correo: " + detalle.getString("correo")));
            panel.add(label("Tipo: " + detalle.getString("tipo_paciente")));
            panel.add(label("Peso: " + detalle.getDouble("peso") + " kg"));
            // ← Cambia "altura" por "talla" si el JSON usa esa clave
            panel.add(label("Talla: " + detalle.getDouble("talla") + " cm"));
            panel.add(label("Enfermedades: " + detalle.getString("enfermedades")));
            panel.add(label("Medico: " + detalle.getString("medico")));
            panel.add(label("Fecha: " + detalle.getString("fecha")));
            panel.add(label("Hora: " + detalle.getString("hora")));
            panel.add(label("Especialidad: " + detalle.getString("especialidad")));

            JTextArea txtRazon = new JTextArea("Razón del rechazo (opcional)", 3, 30);
            txtRazon.setLineWrap(true);
            txtRazon.setWrapStyleWord(true);

            JButton btnAprobar = new JButton("Aprobar Cita");
            JButton btnRechazar = new JButton("Rechazar Cita");

            btnAprobar.addActionListener(e -> aprobar(true, null));
            btnRechazar.addActionListener(e -> aprobar(false, txtRazon.getText()));

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botones.add(btnAprobar);
            botones.add(btnRechazar);

            panel.add(new JScrollPane(txtRazon));
            panel.add(botones);

            // -------------------------------------------------
            add(panel);
            pack();                                   // ← calcula tamaño real
            setMinimumSize(getSize());                // evita que se encoja demasiado
            parent.agregarFrame(this);                // ← usa el helper centralizado
            // -------------------------------------------------

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el detalle de la cita:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();   // cerramos el frame si no hay datos
        }
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
        return new JLabel("<html>" + html + "</html>");
    }
}