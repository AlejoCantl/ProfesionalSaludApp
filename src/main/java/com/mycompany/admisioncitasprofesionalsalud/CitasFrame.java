package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CitasFrame extends JInternalFrame {
    private JTable tabla;
    private DefaultTableModel modelo;
    private final ProfesionalSaludApp parent; 
    private final String token; // Hacemos el token una variable de instancia

    public CitasFrame(ProfesionalSaludApp parent, String token) {
        super("Citas Pendientes", true, true, true, true);
        this.parent = parent;
        this.token = token; // Asignamos el token
        
        initUI(); // initUI ya no necesita el token como argumento
        
        // -----------------------------------------------------------------
        // Ajuste de tamaño final y centrado
        setPreferredSize(new Dimension(850, 500)); 
        pack();
        setMinimumSize(getSize());
        parent.agregarFrame(this); 
        // -----------------------------------------------------------------
    }

    private void initUI() {
        // Usar BorderLayout para el JInternalFrame
        setLayout(new BorderLayout()); 

        String[] columnas = {"ID", "Paciente", "Identificación", "Médico", "Fecha y Hora", "Especialidad"};
        modelo = new DefaultTableModel(columnas, 0) {
            // Impedir la edición de celdas
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        
        // --- 1. MEJORAS VISUALES Y CENTRADO DE LA TABLA ---
        
        // ... (Renderer, estilo de tabla, etc. - Mantenemos el código original) ...
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tabla.setShowGrid(true); 
        tabla.setGridColor(Color.LIGHT_GRAY); 
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tabla.getTableHeader().setBackground(new Color(220, 220, 220)); 
        tabla.getTableHeader().setForeground(new Color(50, 50, 50)); 
        tabla.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY)); 

        tabla.setRowHeight(25);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 30)); 

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40); 
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 0, 10), 
            scrollPane.getBorder()
        ));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // --- 2. Panel de Botones ---
        
        JButton btnDetalle = new JButton("Ver Detalles");
        JButton btnAprobar = new JButton("✅ Aprobar"); // Nuevo botón
        JButton btnRechazar = new JButton("❌ Rechazar"); // Nuevo botón

        // Lógica para Ver Detalles
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int citaId = (int) modelo.getValueAt(row, 0); 
                new DetalleCitaFrame(parent, token, citaId);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una cita", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Lógica para Aprobar
        btnAprobar.addActionListener(e -> aprobarCitaDesdeTabla(true));

        // Lógica para Rechazar
        btnRechazar.addActionListener(e -> aprobarCitaDesdeTabla(false));

        // Agrupación de botones
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Espacio horizontal de 15px
        botonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
        botonPanel.add(btnDetalle);
        botonPanel.add(btnAprobar);
        botonPanel.add(btnRechazar);
        add(botonPanel, BorderLayout.SOUTH);
        
        // Cargar datos
        cargarCitas(); // Ya no necesita el token como argumento
    }

    // Método que maneja la lógica de aprobación/rechazo desde la tabla
    private void aprobarCitaDesdeTabla(boolean aprobado) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citaId = (int) modelo.getValueAt(row, 0);
        String razon = "";

        if (!aprobado) {
            // Si es rechazo, pedimos la razón (opcional)
            razon = JOptionPane.showInputDialog(this, 
                "Ingresa la razón del rechazo (opcional):", 
                "Rechazar Cita #" + citaId, 
                JOptionPane.QUESTION_MESSAGE
            );
            // Si el usuario cancela (razon es null), detenemos la operación.
            if (razon == null) return;
        }

        try {
            ApiService.aprobarCita(token, citaId, aprobado, razon);
            
            JOptionPane.showMessageDialog(this,
                aprobado ? "Cita #" + citaId + " aprobada con éxito." : "Cita #" + citaId + " rechazada con éxito.",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
            
            cargarCitas(); // Recargar la tabla para reflejar el cambio
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar la cita:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // El método cargarCitas ahora no necesita el token como argumento, ya lo tiene como instancia
    public void cargarCitas() {
        try {
            JSONArray citas = ApiService.getCitasPendientes(token);
            modelo.setRowCount(0);
            for (int i = 0; i < citas.length(); i++) {
                JSONObject c = citas.getJSONObject(i);
                modelo.addRow(new Object[]{
                    c.getInt("id"), 
                    c.getString("paciente"), 
                    c.getString("identificacion"), 
                    c.getString("medico"), 
                    c.getString("fecha") + " "+ c.getString("hora"), 
                    c.getString("especialidad")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }
}