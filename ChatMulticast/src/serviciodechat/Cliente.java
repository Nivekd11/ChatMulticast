package serviciodechat;

import Interfaz.PanelFondo;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Cliente extends javax.swing.JFrame implements Runnable, ActionListener, DocumentListener {
    public final static String GRUPO = "GRUPO";
    
    public final int DESCONOCIDO_ID     = 0;
    public final int INICIO_ID          = 1;
    public final int MENSAJE_PRIVADO_ID = 2;
    public final int MENSAJE_PUBLICO_ID = 3;
    public final int FIN_ID             = 4;
    
    private String nombre = null;
    private String nombreDestino = "";
    
    private AnalisisDeMensajes am;
    HashMap<String, String> conversaciones = new HashMap<>();//K = Nombre, V = Mensajes
    HashMap<String, JButton> usuarios = new HashMap<>(); //K = nombre , V = jbutton
    JButton grupo;
    public ArrayList<Mensaje> listaMensajes = new ArrayList<Mensaje>();
    private final PanelFondo contenedor = new PanelFondo("../Interfaz/cuadro-blanco.png");
    
    public Cliente() throws IOException {
        
        setContentPane(contenedor);
        initComponents();
        init();
        this.setResizable(false);//no permite que sea redimencionable
        this.setLocationRelativeTo(null);//la ventana aparece al centro de la pantalla
        this.setTitle(nombre);
        am = new AnalisisDeMensajes();
        
        Conversacion.setContentType("text/html");
        Conversacion.setEditable(false);
        
         addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                try {
                    close();
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            }
        });
        
    }
    private void close() throws IOException{
        if (JOptionPane.showConfirmDialog(rootPane, "??Deseas salir?",
                "Salir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            AdministradorDeOperaciones.getInstance().salirSesion(nombre);
            System.exit(0);
        }
    }
    private void iniciarChat() throws IOException
    {
        LogIn login = new LogIn(new javax.swing.JFrame(), true);
        login.setVisible(true);
        nombre = login.getNombre();
        if(nombre == null)
        {
            System.exit(0);
        }
        else
        {
            nombre.trim();
            AdministradorDeOperaciones.getInstance().iniciarSesion(nombre);
        }
    }

    private void init() throws IOException
    {
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass()
                        .getResource("/Interfaz/icono.png"));
        
        iniciarChat();
        Enviar.addActionListener(this);
        Enviar.setEnabled(false);
        Texto.getDocument().addDocumentListener(this);
        grupo = new JButton("GRUPO");
        grupo.addActionListener(this);
        conversaciones.put(GRUPO, "");
        usuarios.put(GRUPO, grupo);
        actualizarBotones();
    }
    
    private void mostrarMensaje(Mensaje mensaje)
    {
       // System.out.print("ID: " + mensaje.getId() + "\n");
        if(mensaje.getNombreOrigen() != null)
        {
       //     System.out.print("Origen: " + mensaje.getNombreOrigen() + "\n");
        }
        
        if(mensaje.getNombreDestino() != null)
        {
        //    System.out.print("Destino: " + mensaje.getNombreDestino() + "\n");
        }
        
        if(mensaje.getMensaje() != null)
        {
       //     System.out.print("Mensaje: " + mensaje.getMensaje() + "\n");
        }
    }
    
    private void actualizar() throws IOException
    {
        Mensaje mensaje = AdministradorDeOperaciones.getInstance().recibe();
        mostrarMensaje(mensaje); 
        if (mensaje.getNombreOrigen() != null) {
            switch (mensaje.getId()) {
                case INICIO_ID:
                    if (!mensaje.getNombreOrigen().equals(nombre)) {
                        iniciarSesion(mensaje.getNombreOrigen());
                    }
                    break;
                case FIN_ID:
                    finSesion(mensaje.getNombreOrigen());
                    break;
                case MENSAJE_PRIVADO_ID:
                    if (!mensaje.getNombreOrigen().equals(nombre)) 
                    {
                        if (mensaje.getNombreDestino().equals(nombre)) //si es para este cliente
                        {
                            visualizarMensajePrivado(mensaje);
                        }
                    }
                    break;
                case MENSAJE_PUBLICO_ID:

                    if (mensaje.getNombreOrigen().equals(nombre)) {
                        mensaje.setNombreOrigen("T??");
                    }
                    visualizarMensajePublico(mensaje);
                    break;
                default:

            }
        }

    }
    
    private void visualizarMensajePublico(Mensaje mensaje)
    {
        
        String   msj = conversaciones.get(GRUPO) + "<br>" + mensaje.getNombreOrigen() 
                  + ": " + mensaje.getMensaje();
        
        conversaciones.put(GRUPO, msj );
        if(nombreDestino.equals(GRUPO))
        {
            Conversacion.setText(am.formatoAMensaje(msj));
            grupo.setForeground(Color.white);
            grupo.setBackground(new Color(59,89,152));
        }else
        {
            grupo.setForeground(Color.white);
            grupo.setBackground(new Color(234,161,27));
        }
    }
    
    private void visualizarMensajePrivado(Mensaje mensaje)
    {   
        String   msj = conversaciones.get(mensaje.getNombreOrigen()) + "<br>" + mensaje.getNombreOrigen() 
                  + ": " + mensaje.getMensaje();                
        conversaciones.put(mensaje.getNombreOrigen(), msj ); //Visualiza el mensaje;
        if(nombreDestino.equals(mensaje.getNombreOrigen()))
        {
            Conversacion.setText(am.formatoAMensaje(msj));
            usuarios.get(mensaje.getNombreOrigen()).setForeground(Color.white);
            usuarios.get(mensaje.getNombreOrigen()).setBackground(new Color(59,89,152));
        }else
        {
            usuarios.get(mensaje.getNombreOrigen()).setForeground(Color.white);
            usuarios.get(mensaje.getNombreOrigen()).setBackground(new Color(234,161,27));
        }
    }        
        
    private void finSesion(String usuario)
    {
        conversaciones.remove(usuario);
        usuarios.remove(usuario);
        actualizarBotones();
        if(usuario.equals(nombreDestino))
        {
            nombreDestino = "";
            setTitle(nombre);
        }
        habilitarEnvio();
    }
    
    private void iniciarSesion(String nombreNuevo) throws IOException
    {   
        if (!existiaUsuario(nombreNuevo)) 
        {
            AdministradorDeOperaciones.getInstance().iniciarSesion(nombre);
            actualizarBotones();
        }
    }
    
    private void actualizarBotones()
    {
        JPanel panel = new JPanel(new GridLayout(50,0));
        panel.setBackground(Color.BLACK);
        panel.add(grupo);
        Collection<JButton> usuariosConectados = usuarios.values();
        for (JButton u : usuariosConectados) 
            panel.add(u);
        UsuariosConectados.setViewportView(panel);
        if(usuariosConectados.size() == 1)
            grupo.setEnabled(false);
        else
            grupo.setEnabled(true);
            
    }
    
    private boolean existiaUsuario(String nombre)
    {
        if(conversaciones.get(nombre) == null)
        {
            conversaciones.put(nombre, nombre + " ha iniciado sesi??n");
            JButton b = new JButton(nombre);
            b.setForeground(Color.white);
            b.setBackground(new Color(0,204,204));
            b.addActionListener(this);
            usuarios.put(nombre, b);
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        Conversacion = new javax.swing.JTextPane();
        UsuariosConectados = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        Texto = new javax.swing.JTextArea();
        Enviar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(254, 254, 254));

        Conversacion.setBackground(new java.awt.Color(0, 204, 204));
        Conversacion.setForeground(new java.awt.Color(0, 0, 0));
        Conversacion.setCaretColor(new java.awt.Color(102, 204, 255));
        Conversacion.setDisabledTextColor(new java.awt.Color(255, 204, 204));
        Conversacion.setSelectedTextColor(new java.awt.Color(102, 102, 255));
        jScrollPane2.setViewportView(Conversacion);
        Conversacion.setBackground(new Color(255,204,205));

        UsuariosConectados.setBackground(new java.awt.Color(254, 254, 254));

        Texto.setBackground(new java.awt.Color(255, 255, 204));
        Texto.setColumns(20);
        Texto.setRows(5);
        jScrollPane3.setViewportView(Texto);
        Texto.setBackground(new Color(255,255,204));

        Enviar.setText("Enviar");

        jLabel1.setFont(new java.awt.Font("Juice ITC", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ChatMulticast");

        jLabel2.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel2.setText("Mensaje:");

        jLabel3.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel3.setText("Chat:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(12, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(Enviar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 792, Short.MAX_VALUE)
                            .addComponent(UsuariosConectados, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UsuariosConectados, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(Enviar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        
        Cliente c =new Cliente();
        c.setVisible(true);
        Runnable run = c;
        Thread thread = new Thread(c);
        thread.start();
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane Conversacion;
    private javax.swing.JButton Enviar;
    private javax.swing.JTextArea Texto;
    private javax.swing.JScrollPane UsuariosConectados;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        while(true)
        {
            try {
                actualizar();
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if(ae.getSource().equals(Enviar))
        {
            try {
                
                if(nombreDestino.equals(GRUPO))
                {
                    
                    AdministradorDeOperaciones.getInstance()
                        .mensajeASala(Texto.getText(), nombre);
                }
                else
                {   
                    AdministradorDeOperaciones.getInstance()
                        .mensajePrivado(nombre, nombreDestino, Texto.getText());
                }
                
                Texto.setText("");
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            JButton b = (JButton) ae.getSource();
            b.setForeground(Color.white);
            b.setBackground(new Color(204,0,205));
            nombreDestino = b.getText();
            Conversacion.setText(am.formatoAMensaje(conversaciones.get(nombreDestino)));
            this.setTitle(nombre + " Habla con:  " + nombreDestino);
            habilitarEnvio();
        }
    }

    private boolean analizarTextoAEnviar(String Mensaje)
    {
        int tam = Mensaje.length(); 
        if( tam > 0)
        {
            char c;
            for(int i = 0; i < tam; i++)
            {
                c = Mensaje.charAt(i);
                if(c != ' ' && c != 10 )
                    return true;
            }
        }
        return false;
    }
            
    private void habilitarEnvio()
    {
        if(analizarTextoAEnviar(Texto.getText()) && nombreDestino.length() > 0)
            Enviar.setEnabled(true);
        else
            Enviar.setEnabled(false);   
    }
    
    @Override
    public void insertUpdate(DocumentEvent de) 
    {
        habilitarEnvio();
    }

    @Override
    public void removeUpdate(DocumentEvent de) 
    {
        habilitarEnvio();
    }

    @Override
    public void changedUpdate(DocumentEvent de) 
    {
        habilitarEnvio();
    }
}
