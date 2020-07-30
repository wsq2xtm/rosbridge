package temperature_recognition;

import com.gary.ros.message.CompressedImage;
import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
import com.gary.ros.message.Time;

@MessageType(string = "temperature_recognition/app_msg")
public class app_msg extends Message {
    public float setTemperature;
    public byte setTemperatureStatus;

    public float currentTemperature;
    public byte currentTemperatureStatus;

    public Time recognitionTime;
    public CompressedImage recognitionImage;
}
