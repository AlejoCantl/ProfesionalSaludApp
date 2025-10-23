package com.mycompany.admisioncitasprofesionalsalud;

import org.json.JSONObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import org.json.JSONArray;

public class PerfilFrame extends JInternalFrame {
    private final ProfesionalSaludApp parent;

    public PerfilFrame(ProfesionalSaludApp parent, String token) {
        super("Perfil del Profesional", true, true, true, true);
        this.parent = parent;
        
        cargarPerfil(token);
        
        // -----------------------------------------------------------------
        // Ajuste de tamaño final y centrado
        setPreferredSize(new Dimension(450, 400)); // Establecer un tamaño preferido
        pack(); // Ajusta al contenido, respetando el preferido si el contenido es menor
        setMinimumSize(getSize()); 
        // -----------------------------------------------------------------
        
        parent.agregarFrame(this); // Agregar y centrar después de cargar
    }

    private void cargarPerfil(String token) {
        try {
            JSONObject perfil = ApiService.getPerfil(token);
            
            // Panel principal con BorderLayout para la estructura
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Margen exterior

            // Panel contenedor para la información (usando Y_AXIS para la estructura vertical)
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            
            // --- 1. Información General ---
            JPanel generalPanel = createTitledPanel("Datos Generales");
            generalPanel.add(label("<b>Nombre:</b> " + perfil.getString("nombre") + " " + perfil.getString("apellido")));
            generalPanel.add(label("<b>Correo:</b> " + perfil.getString("correo")));
            generalPanel.add(label("<b>Ubicación:</b> " + perfil.getString("ubicacion")));

            // --- 2. Información Laboral/Específica ---
            JSONObject datos = perfil.getJSONObject("datos_especificos");
            JPanel laboralPanel = createTitledPanel("Información Laboral");
            laboralPanel.add(label("<b>Cargo:</b> " + datos.optString("cargo", "N/A")));
            laboralPanel.add(label("<b>Fecha de Ingreso:</b> " + datos.optString("fecha_ingreso", "N/A")));
            
            // Intenta obtener especialidades si existen
            if (datos.has("especialidades") && datos.get("especialidades") instanceof JSONArray) {
                JSONArray especialidades = datos.getJSONArray("especialidades");
                String espList = formatArray(especialidades);
                laboralPanel.add(label("<b>Especialidades:</b> " + (espList.isEmpty() ? "No asignadas" : espList)));
            } else if (datos.has("especialidad")) {
                laboralPanel.add(label("<b>Especialidad Principal:</b> " + datos.getString("especialidad")));
            }


            // --- 3. Ensamblar Paneles ---
            infoPanel.add(generalPanel);
            infoPanel.add(Box.createVerticalStrut(15)); // Espaciador
            infoPanel.add(laboralPanel);
            
            // Si hay otros datos que no caben en las categorías anteriores, puedes agregarlos aquí.
            
            // Añadir el infoPanel al centro del mainPanel, usando JScrollPane por si acaso.
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            
            // -------------------------------------------------
            add(new JScrollPane(mainPanel));
            pack(); // Ajusta al nuevo contenido
            setMinimumSize(getSize());
            // -------------------------------------------------
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el perfil. Verifique la conexión o el formato de datos.", 
                                          "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose(); // Cerramos el frame si no se puede cargar
        }
    }
    
    /**
     * Helper para crear un panel con un título y BoxLayout (Y_AXIS).
     */
    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // TitledBorder con margen interno
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)), // Borde gris sutil
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10) // Padding interno
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Asegura la alineación en el contenedor padre
        return panel;
    }
    
    /**
     * Helper para formatear un array JSON a una cadena separada por comas.
     */
    private String formatArray(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            sb.append(array.getString(i));
            if (i < array.length() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Helper para crear un JLabel con formato HTML y alineación a la izquierda.
     */
    private JLabel label(String html) {
        JLabel lbl = new JLabel("<html>" + html + "</html>");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}