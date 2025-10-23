package com.mycompany.admisioncitasprofesionalsalud;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class CitasFrame extends JInternalFrame {
    private JTable tabla;
    private DefaultTableModel modelo;
    private final ProfesionalSaludApp parent; 
    private final String token; 
    
    public CitasFrame(ProfesionalSaludApp parent, String token) {
        super("Citas Pendientes", true, true, true, true);
        this.parent = parent;
        this.token = token; 
        
        initUI(); 
        
        setPreferredSize(new Dimension(850, 500)); 
        pack();
        setMinimumSize(getSize());
        parent.agregarFrame(this); 
    }
    
    // Necesario para que RechazarCitaFrame pueda agregarse al DesktopPane
    public JDesktopPane getDesktopPane() {
        return parent.getDesktopPane(); 
    }
    
    // Necesario para que RechazarCitaFrame pueda acceder al token de API
    public String getToken() {
        return token;
    }

    private void initUI() {
        setLayout(new BorderLayout()); 

        String[] columnas = {"ID", "Paciente", "Identificación", "Médico", "Fecha y Hora", "Especialidad"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        
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
            new EmptyBorder(10, 10, 10, 10), 
            scrollPane.getBorder()
        ));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // --- Panel de Botones ---
        JButton btnDetalle = new JButton("Ver Detalles");
        JButton btnAprobar = new JButton("✅ Aprobar"); 
        JButton btnRechazar = new JButton("❌ Rechazar"); 

        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int citaId = (int) modelo.getValueAt(row, 0); 
                // *** FUNCIONALIDAD RESTAURADA ***
                new DetalleCitaFrame(parent, token, citaId); 
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una cita", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Lógica de Aprobar: sin razón
        btnAprobar.addActionListener(e -> aprobarCitaDesdeTabla(true, token, "")); 
        
        // Lógica de Rechazo: llama a la nueva ventana interna
        btnRechazar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona una cita de la tabla para rechazar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int citaId = (int) modelo.getValueAt(row, 0);
            
            new RechazarCitaFrame(this, citaId);
        }); 

        // Agrupación de botones
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
        botonPanel.add(btnDetalle);
        botonPanel.add(btnAprobar);
        botonPanel.add(btnRechazar);
        
        add(botonPanel, BorderLayout.SOUTH);
        
        cargarCitas(); 
    }
    
    // Método llamado por los botones APROBAR y por RechazarCitaFrame
    public void aprobarCitaDesdeTabla(boolean aprobado, String token, String razon) {
        int row = tabla.getSelectedRow();
        
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citaId = (int) modelo.getValueAt(row, 0);
        
        if (!aprobado && razon.isEmpty()) {
             int confirm = JOptionPane.showConfirmDialog(this, 
                 "No has ingresado una razón. ¿Deseas rechazar la cita de todas formas?", 
                 "Confirmar Rechazo", 
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE);
             
             if (confirm != JOptionPane.YES_OPTION) {
                 return; 
             }
        }
        
        String motivoFinal = aprobado ? "" : razon;

        try {
            // *** FUNCIONALIDAD RESTAURADA ***
            ApiService.aprobarCita(token, citaId, aprobado, motivoFinal);
            
            JOptionPane.showMessageDialog(this,
                aprobado ? "Cita #" + citaId + " aprobada con éxito." : "Cita #" + citaId + " rechazada con éxito.",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
            
            cargarCitas(); 
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar la cita:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void cargarCitas() {
        try {
            // *** FUNCIONALIDAD RESTAURADA ***
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