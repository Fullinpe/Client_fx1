package sample;

import gnu.io.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;

public class Controller implements Initializable {

	@FXML
	Label label1;
	@FXML
	ComboBox cb1;
	@FXML
	ComboBox cb2;
	@FXML
	TextArea ta1,ta2,ta3;
	@FXML
	CheckBox ck;
	@FXML
	Button bd,bs;
	SerialPort serialPort;
	int baudRate=115200;
	String Comm;
	static List<String> mCommList;
	byte[] bytes;

	byte[] bytesimg=new byte[20240];
	byte[] byteheat=new byte[2000];

	int indexheat=0;
	int index=0;
	int flag=0;
	Image image;
	ImageView imv;
	Image image1;
	Stage imgstage;
	int maxtemp,mintemp;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		cb2.getItems().addAll(
				"1200",
				"2400",
				"4800",
				"9600",
				"14400",
				"19200",
				"38400",
				"43000",
				"57600",
				"76800",
				"115200",
				"128000",
				"230400",
				"256000",
				"460800",
				"921600",
				"1382400");
		cb2.getSelectionModel().select(10);

		bytesimg[0]=-1;
		bytesimg[1]=-40;

		imgstage=new Stage();
		imgstage.setTitle("接收图像");
		image1= new Image(this.getClass().getResource("/res/efg_icon.png").toString(), 100, 100, false, false);
		Image image2= new Image(this.getClass().getResource("/res/image-test.jpg").toString(), 800,500,false,false);
		imgstage.getIcons().add(image1);
		imv=new ImageView(image2);
		VBox vBox=new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().add(imv);
		imgstage.setX(-1000);
		imgstage.setY(100);
		imgstage.setScene(new Scene(vBox));




		mServer ms=new mServer(cb1);
		ms.start();
		ta3.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if(newValue.length()>0&&(int)newValue.charAt(newValue.length()-1)==10) {
					ta3.setText(oldValue);
					bsfun(new ActionEvent());

				}
			}
		});

	}

	@FXML
	protected void bdfun(ActionEvent event) {
		closeSerialPort(serialPort);
		//开启端口
		if(Comm!=null&&baudRate>0&&bd.getText()!="关闭串口")
			serialPort = openSerialPort(Comm, baudRate);
		//设置串口的listener
		if(serialPort!=null&&bd.getText()!="关闭串口")
			setListenerToSerialPort(serialPort, new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent arg0) {
					if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
						//数据通知
						bytes = readData(serialPort);
						for(int len=0;len<bytes.length;len++)
						{
							//System.out.println(flag+"-"+Byte.toString(bytes[len]));
							if(flag==2||flag==3)
							{
								if(index<20240)
								{
									bytesimg[index]=bytes[len];
									index++;
								}
								else
								{
									index=2;
									flag=0;
									System.out.println("Error");
								}
							}

							if(flag==1&&bytes[len]==-40)
								flag=2;
							else if(flag==1)
								flag=0;
							else if(flag==0&&bytes[len]==-1)
								flag=1;
							else if(flag==3&&bytes[len]==-39)
							{

								//TODO
								image= new Image(new ByteArrayInputStream(bytesimg));
							Platform.runLater(new Runnable() {
								@Override
								public void run() {

									imv.setImage(image);
								}
							});
								flag=0;
								System.out.println(Byte.toString(bytesimg[0])+Byte.toString(bytesimg[1])+Byte.toString(bytesimg[2])
										+Byte.toString(bytesimg[3])+Byte.toString(bytesimg[4])+Byte.toString(bytesimg[5])
										+Byte.toString(bytesimg[6])+Byte.toString(bytesimg[7])+Byte.toString(bytesimg[8])
										+Byte.toString(bytesimg[9])+Byte.toString(bytesimg[index-2])+Byte.toString(bytesimg[index-1]));

								System.out.println("Size:"+index);
								bytesimg=new byte[20240];
								bytesimg[0]=-1;
								bytesimg[1]=-40;
								index=2;

							}else if(flag==3)
								flag=2;
							else if(flag==2&&bytes[len]==-1)
								flag=3;
						}
//					Platform.runLater(new Runnable() {
//						@Override
//						public void run() {
//							if(bytes!=null)
//							{
//								ta1.appendText(new String(bytes));
//							}
//
//						}
//					});
						//System.out.println("GengXin->"+Thread.currentThread());
					}
				}
			});
//			setListenerToSerialPort(serialPort, new SerialPortEventListener() {
//				@Override
//				public void serialEvent(SerialPortEvent arg0) {
//					if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
//						//数据通知
//						bytes = readData(serialPort);
//						for(int len=0;len<bytes.length;len++)
//						{
//							//System.out.println(flag+"-"+Byte.toString(bytes[len]));
//							if(flag==2||flag==3)
//							{
//								if(indexheat<2000)
//								{
//									byteheat[indexheat]=bytes[len];
//									indexheat++;
//								}
//								else
//								{
//									indexheat=2;
//									flag=0;
//									System.out.println("Error");
//								}
//							}
//
//							if(flag==1&&bytes[len]==-42)
//								flag=2;
//							else if(flag==1)
//								flag=0;
//							else if(flag==0&&bytes[len]==-1)
//								flag=1;
//							else if(flag==3&&bytes[len]==-41)
//							{
//
//								//TODO
//								if(indexheat==1538)
//								{
//									int[] intbuf=new int[768];
//									for (int sd=0;sd<768;sd++)
//										intbuf[sd]=((byteheat[sd*2+1]&0xff)<<8)+(byteheat[sd*2]&0xff);
//									mintemp=65535;
//									maxtemp=0;
//									for (int sd=0;sd<768;sd++)
//									{
//										if(intbuf[sd]<mintemp)
//											mintemp=intbuf[sd];
//										if(maxtemp<intbuf[sd])
//											maxtemp=intbuf[sd];
//									}
////								mintemp=1000;
////								maxtemp=5000;
//									float dot=maxtemp-mintemp;
//									for (int sd=0;sd<768;sd++)
//									{
////										intbuf[sd]= (int) ((intbuf[sd]-mintemp)/dot*240);
////										intbuf[sd]= Math.min(intbuf[sd], maxtemp);
////										intbuf[sd]= Math.max(intbuf[sd], mintemp);
//										System.out.print(intbuf[sd]+"-");
//									}
//									for (int sd=0;sd<768;sd++)
//									{
//										intbuf[sd]= Color.HSBtoRGB((float) (0.667-((intbuf[sd]-mintemp)/dot)),1,1);
//
//									}
//
//									Image img;
//									BufferedImage bufImage = new BufferedImage(32, 24,BufferedImage.TYPE_INT_RGB);
//
//									bufImage.setRGB(0,0,32,24,intbuf,0,32);
//									bufImage=resize(bufImage,640,480);
//									ByteArrayOutputStream bs = new ByteArrayOutputStream();
//									ImageOutputStream imOut;
//									try {
//										imOut = ImageIO.createImageOutputStream(bs);
//										ImageIO.write(bufImage, "jpg", imOut);
//										InputStream inputStream = new ByteArrayInputStream(bs.toByteArray());
//										img=new Image(inputStream);
//										Platform.runLater(() ->  imv.setImage(img));
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
//									System.out.println(  Byte.toString(byteheat[0])+Byte.toString(byteheat[1])+Byte.toString(byteheat[2])
//											+Byte.toString(byteheat[3])+Byte.toString(byteheat[4])+Byte.toString(byteheat[5])
//											+Byte.toString(byteheat[6])+Byte.toString(byteheat[7])+Byte.toString(byteheat[8])
//											+Byte.toString(byteheat[9])+Byte.toString(byteheat[indexheat-2])+Byte.toString(byteheat[indexheat-1]));
//
//									System.out.println("Size:"+indexheat+"_____"+"max:"+maxtemp+"min:"+mintemp);
//
//								}
//								else
//									System.out.println("Error_lose_data  size:"+indexheat);
//
//								flag=0;
//								byteheat=new byte[2000];
//								indexheat=0;
//
//							}else if(flag==3)
//								flag=2;
//							else if(flag==2&&bytes[len]==-1)
//								flag=3;
//						}
//					}
//				}
//			});
		if(Comm!=null&&baudRate>0&&bd.getText()!="关闭串口")
			bd.setText("关闭串口");
		else
			bd.setText("打开串口");
	}
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		return dimg;
	}
	@FXML
	protected void bsfun(ActionEvent event) {
		if(ta3.getText().length()!=0&&bd.getText()=="关闭串口")
		{
			String sendData="";
			if(ck.isSelected())
				sendData=ta3.getText()+"\r\n";
			else
				sendData=ta3.getText();
			ta2.appendText("?"+sendData);
			sendData(serialPort, sendData.getBytes());//发送数据

		}

		else if(bd.getText()!="关闭串口")
		{

			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("警告");
			alert.setHeaderText(null);
			alert.setContentText("请先打开一个串口 !");

			alert.showAndWait();
		}

	}

	public void cb1fun(ActionEvent actionEvent) {
		Comm=cb1.getSelectionModel().getSelectedItem().toString();
		bd.setText("打开串口");
	}

	public void cb2fun(ActionEvent actionEvent) {
		baudRate=Integer.parseInt(cb2.getSelectionModel().getSelectedItem().toString());
		bd.setText("打开串口");
	}

	public void rst1fun(ActionEvent actionEvent) {
		imgstage.show();

	}

	public void rst2fun(ActionEvent actionEvent) {
//		byte by1=-1,by2=-40;
//		int xxx;
//		xxx=(by1&0xff<<8)+(by2&0xff);
		System.out.println("");
		byte[] bytess=new byte[9];
		for (int sd=0;sd<9;sd++)
			if((sd)/3!=1)
				bytess[sd]=0;
			else
				bytess[sd]=-1;
		Image img;
		BufferedImage bufImage = new BufferedImage(3, 3,BufferedImage.TYPE_INT_RGB);

		int[] intbuf=new int[9];
		for (int sd=0;sd<9;sd++)
			intbuf[sd]=0x00000000|0x5f<<16;
		bufImage.setRGB(0,0,3,3,intbuf,0,3);
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut;
		try {
			imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(bufImage, "jpg", imOut);
			InputStream inputStream = new ByteArrayInputStream(bs.toByteArray());
			img=new Image(inputStream);
			imv.setImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		ta1.setText("");
//		ta2.setText("");
	}

	/**
	 * 给串口设置监听
	 *
	 * @param serialPort
	 * @param listener
	 */
	public static void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) {
		try {
			//给串口添加事件监听
			serialPort.addEventListener(listener);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		serialPort.notifyOnDataAvailable(true);//串口有数据监听
		serialPort.notifyOnBreakInterrupt(true);//中断事件监听
	}
	/**
	 * 获得系统可用的端口名称列表
	 *
	 * @return 可用端口名称列表
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getSystemPort() {
		List<String> systemPorts = new ArrayList<>();
		//获得系统可用的端口
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();//获得端口的名字
			systemPorts.add(portName);
		}
		//System.out.println("系统可用端口列表:" + systemPorts);
		return systemPorts;
	}
	/**
	 * 开启串口
	 *
	 * @param serialPortName 串口名称
	 * @param baudRate       波特率
	 * @return 串口对象
	 */
	public static SerialPort openSerialPort(String serialPortName, int baudRate) {

		try {
			//通过端口名称得到端口
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
			//打开端口，（自定义名字，打开超时时间）
			CommPort commPort = portIdentifier.open(serialPortName, 2222);
			//判断是不是串口
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				//设置串口参数（波特率，数据位8，停止位1，校验位无）
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				System.out.println("开启串口成功，串口名称:" + serialPortName);
				return serialPort;
			} else {
				//是其他类型的端口
				throw new NoSuchPortException();
			}
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 关闭串口
	 *
	 * @param serialPort 要关闭的串口对象
	 */
	public static void closeSerialPort(SerialPort serialPort) {
		if (serialPort != null) {
			serialPort.close();
			System.out.println("关闭了串口：" + serialPort.getName());
			serialPort = null;
		}
	}

	/**
	 * 向串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param data       发送的数据
	 */
	public static void sendData(SerialPort serialPort, byte[] data) {
		OutputStream os = null;
		try {
			os = serialPort.getOutputStream();//获得串口的输出流
			os.write(data);
			//os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
					os = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从串口读取数据
	 *
	 * @param serialPort 要读取的串口
	 * @return 读取的数据
	 */
	public static byte[] readData(SerialPort serialPort) {
		InputStream is = null;
		byte[] bytes = null;
		try {
			is = serialPort.getInputStream();//获得串口的输入流
			int bufflenth = is.available();//获得数据长度
			while (bufflenth != 0) {
				bytes = new byte[bufflenth];//初始化byte数组
				is.read(bytes);
				bufflenth = is.available();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

}

class mServer extends ScheduledService<Integer>
{
	ComboBox cb1;

	public mServer(ComboBox cb1) {
		this.cb1 = cb1;
	}

	@Override
	protected Task<Integer> createTask() {
		return new Task<Integer>() {
			@Override
			protected void updateValue(Integer value) {
				super.updateValue(value);
				cb1.getItems().setAll(Controller.mCommList);
				if(Controller.mCommList.size()>0)
					cb1.getSelectionModel().select(0);
			}

			@Override
			protected Integer call() throws Exception {
				Thread.sleep(500);

				//获得系统端口列表
				Controller.mCommList=Controller.getSystemPort();

				return 0;
			}
		};
	}
}