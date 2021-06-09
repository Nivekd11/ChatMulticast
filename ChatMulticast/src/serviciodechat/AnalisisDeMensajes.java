package serviciodechat;

/**
 *
 * @author miguel angel amador nava
 */
public class AnalisisDeMensajes {

    // Risa -> :)
    private final String RISA = " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/risa.png").toString()
            + " width='30' height='30' alt='emoji'> ";

    // Amor -> :o
    private final String AMOR = " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/amor.png").toString()
            + " width='30' height='30' alt='emoji'> ";

    // Enojo -> :S
    private final String ENOJO = " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/enojo.png").toString()
            + " width='30' height='30' alt='emoji'> ";

    // Loco -> _loco_
    private final String LOCO = " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/loco.gif").toString()
            + " width='30' height='30' alt='emoji'> ";

    // Homero -> _Homero_
    private final String HOMERO = " <img src="
            + "http://tusimagenesde.com/wp-content/uploads/2015/01/gifs-animados-5.gif"
            + " width='100' height='100' alt='emoji'> ";
    
    private final String ABRAZO = " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/abrazo.gif").toString()
            + " width='100' height='100' alt='emoji'> ";
    
     private final String GATO= " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/cat.png").toString()
            + " width='30' height='30' alt='emoji'> ";
    
     private final String TRISTE= " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/triste.png").toString()
            + " width='30' height='30' alt='emoji'> ";
      private final String LENGUA= " <img src=" + this.getClass().getClassLoader()
            .getResource("Emojis/lengua.png").toString()
            + " width='30' height='30' alt='emoji'> ";

    public String formatoAMensaje(String mensaje) {
        int tam = mensaje.length();
        char c;
        int i = 0;
        String msj = "";
        while (i < tam) {
            c = mensaje.charAt(i);
            if (c == ' ') {
                c = mensaje.charAt(i + 1);
                if (c == ':') {
                    if (mensaje.charAt(i + 3) == ' ') {
                        c = mensaje.charAt(i + 2);
                        if (c == ')') {
                            msj += RISA;
                            i += 3;
                            System.out.print("imgRisa");
                        } else if (c == 'o') {
                            msj += AMOR;
                            i += 3;
                            System.out.print("imgaAmor");
                        } else if (c == 'S') {
                            msj += ENOJO;
                            i += 3;
                            System.out.print("imgEnojo");
                        } else if (c == '(') {
                            msj += TRISTE;
                            i += 3;
                            System.out.print("imgTriste");
                        } else if (c == '3') {
                            msj += GATO;
                            i += 3;
                            System.out.print("imgGatito");
                        } else if (c == 'p') {
                            msj += LENGUA;
                            i += 3;
                            System.out.print("imgLengua");
                        }
                        else {
                            msj += ' ';
                        }
                    } else {
                        msj += ' ';
                    }
                } else if (c == '_') {
                    if (mensaje.charAt(i + 6) == '_') {
                        if ((mensaje.charAt(i + 2) == 'l')
                                && (mensaje.charAt(i + 3) == 'o')
                                && (mensaje.charAt(i + 4) == 'c')
                                && (mensaje.charAt(i + 5) == 'o')) {

                            msj += LOCO;
                            i += 7;
                            System.out.print("imgloco");
                        }
                    } else if (mensaje.charAt(i + 8) == '_') {
                        if ((mensaje.charAt(i + 2) == 'H')
                                && (mensaje.charAt(i + 3) == 'o')
                                && (mensaje.charAt(i + 4) == 'm')
                                && (mensaje.charAt(i + 5) == 'e')
                                && (mensaje.charAt(i + 6) == 'r')
                                && (mensaje.charAt(i + 7) == 'o')) {

                            msj += HOMERO;
                            i += 9;
                            System.out.print("imghomero");
                        }
                    } else if (mensaje.charAt(i + 9) == '_') {
                        if ((mensaje.charAt(i + 2) == 'A')
                                && (mensaje.charAt(i + 3) == 'b')
                                && (mensaje.charAt(i + 4) == 'r')
                                && (mensaje.charAt(i + 5) == 'a')
                                && (mensaje.charAt(i + 6) == 'z')
                                && (mensaje.charAt(i + 7) == 'o')
                                && (mensaje.charAt(i + 8) == 's')) {

                            msj += ABRAZO;
                            i += 10;
                            System.out.print("imgAbrazo");
                        }
                    } else {
                        msj += ' ';
                    }
                } else {
                    msj += ' ';
                }
            } else {
                msj += c;
            }

            i++;
        }
        return "<html> " + msj + "</html>";
    }

    //funcion alterada para solucion rápida 
    public String formatoDescripcion(String descripcion, int tope) {
        int aux = 0;
        String msj = "<html><body>";
        char c;
        String msjF = "";
        for (int i = 0; i < descripcion.length(); i++) {

            if (descripcion.charAt(i) == ' ' || i == tope) {
                if (i < tope) {
                    aux = i;
                } else {
                    if (i == tope && aux == 0) {
                        aux = tope;
                    }
                    msj += descripcion.substring(0, aux) + " <br> ";

                    descripcion = descripcion.substring(aux, descripcion.length());
                    i = 0;
                }
            }
        }
        msj += descripcion;
        msj += "</body></html>";
        //return formatoAMensaje(msj);
        return msj;
    }
}
