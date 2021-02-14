import jpysocket
import Extractor
import os

SIMPLE_PACKET_SIZE = 1024
FILE_PACKET_SIZE = 4 * 1024


# Single-process server
def start_server():
    host = 'localhost'  # Host Name
    port = 5000  # Port Number
    s = jpysocket.jpysocket()  # Create Socket
    s.bind((host, port))  # Bind Port And Host
    s.listen(5)  # listening
    print("Server started ... ")

    while True:
        connection, address = s.accept()  # Accept the Connection
        msg_recv = connection.recv(SIMPLE_PACKET_SIZE)  # Receive msg
        msg_recv = jpysocket.jpydecode(msg_recv)  # Decrypt msg
        if msg_recv == "SendFile":
            print("\nRequest for extracting features")
            msg_recv = connection.recv(SIMPLE_PACKET_SIZE)
            size = int((int(jpysocket.jpydecode(msg_recv))) / FILE_PACKET_SIZE) + 1
            # Only .wav file are accepted!
            f = open("SongTemp.wav", 'wb')  # temporary file, opened in binary
            print("Download of the file ...")
            while size > 0:
                packet = connection.recv(FILE_PACKET_SIZE)
                f.write(packet)
                size -= 1
            f.close()
            print("Extracting features ...")
            result = Extractor.extract_feature("SongTemp.wav")
            print("Features extracted: " + result)
            # The /r/n is necessary for the client side, that needs to receive a complete line
            connection.send(bytes(result + "\r\n", 'UTF-8'))
            os.remove("SongTemp.wav")  # remove the temporary file
        connection.close()
    s.close()
