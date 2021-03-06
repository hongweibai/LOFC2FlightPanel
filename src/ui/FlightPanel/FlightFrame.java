package ui.FlightPanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import Llfc2Constant.LOFC2Constant;
import model.Dispatcher;
import model.infoparser.Parser;
import socket.server.SocketServerHelper.SocketServerHelperListener;
import ui.FlightPanel.Conn.ConnectionPanel;
import ui.FlightPanel.Flight.FlightGroup;
import utils.Log;

public class FlightFrame extends JFrame {
    private static final long serialVersionUID = 2388936908821536696L;
    private final String TAG = "FlightFrame";

    public FlightFrame() {
        // connection status bar
        ConnectionPanel connPanel = new ConnectionPanel();
        FlightGroup flightPanel = new FlightGroup();

        // Root layout
        Container containtPane = getContentPane();
        containtPane.setLayout(new BoxLayout(containtPane, BoxLayout.Y_AXIS));
        connPanel.setAlignmentX(0.0F);
        containtPane.add(connPanel);
        containtPane.add(flightPanel);
        setPreferredSize(new Dimension(500, 177));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 200, screenSize.height - 200);
        Image icon = Toolkit.getDefaultToolkit().getImage(LOFC2Constant.RES_ROOT + "lofc2.png");
        setIconImage(icon);

        Dispatcher.getInstance().getSocketServerHelper()
                .setSocketServerHelperListener(new SocketServerHelperListener() {

                    @Override
                    public void onInfoReceived(String info) {
                        Parser.getInstance().parse(info);
                        flightPanel.setFlightInfo(
                                Double.valueOf(Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.INDICATED_AIR_SPEED)),
                                Double.valueOf(
                                        Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.ALTITUDE_ABOVE_SEALEVEL)),
                                Double.valueOf(Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.ENGINE_RPM_LEFT))
                                        .intValue(),
                                Double.valueOf(Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.ENGINE_RPM_RIGHT))
                                        .intValue(),
                                Integer.valueOf(
                                        Parser.getInstance().get(LOFC2Constant.MISSION_KEY.MYPLANE_OBJ_COUNTRY)),
                                Double.valueOf(Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.VERTICAL_SPEED)),
                                Double.valueOf(Parser.getInstance().get(LOFC2Constant.FLIGHT_KEY.PITCH)));
                        flightPanel.setFlightAttitude(
                                Integer.valueOf(Parser.getInstance().get(LOFC2Constant.ATTITUDE_KEY.GEAR)),
                                Integer.valueOf(Parser.getInstance().get(LOFC2Constant.ATTITUDE_KEY.FLAP)),
                                Integer.valueOf(Parser.getInstance().get(LOFC2Constant.ATTITUDE_KEY.AIRBRAKE)),
                                Integer.valueOf(Parser.getInstance().get(LOFC2Constant.ATTITUDE_KEY.HOOK)));
                    }

                    @Override
                    public void onClientQuit() {
                        connPanel.setDisconnected();
                    }

                    @Override
                    public void onClientAccepted(String clientAddr) {
                        Log.d(TAG, "v 12.13");
                        connPanel.setConnected(clientAddr);
                    }
                });
        new Thread(Dispatcher.getInstance()).start();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        FlightFrame flightPanel = new FlightFrame();
        flightPanel.setAlwaysOnTop(true);
        flightPanel.setTitle("FlightPanel");
        flightPanel.pack();
        flightPanel.setVisible(true);
    }
}
