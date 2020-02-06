import flask
import flask_inputs
import werkzeug
import keras.models
from keras.preprocessing.image import img_to_array
import numpy as np
import cv2

app = flask.Flask(__name__)
@app.route('/', methods = ['GET', 'POST'])
def hello():
    return('hello')

@app.route('/classify/<int:id>', methods = ['GET','POST'])
def handle_request(id):
    if(flask.request.method=='POST'):
        
        imagefile = flask.request.files['image']
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("\nReceived image File name : " + imagefile.filename)
        
        imagefile.save(filename)
        print(filename)


        img = cv2.imread(filename)
        print(img.shape)
        img = cv2.resize(img, (50, 50))
        #(h, w) = img.shape[:2]  
        #center = (w / 2, h / 2)
        #M = cv2.getRotationMatrix2D(center, 90 , 1.0)  
        #img = cv2.warpAffine(img, M, (h, w))
        img = img.astype("float") / 255.0
        img = img_to_array(img)
        img = np.expand_dims(img, axis=0) 
        if(id==1):
            loaded_model = keras.models.load_model('modelasl.h5')
            prediction=loaded_model.predict(img)[0]
            print(prediction)
            label={0:'A',1:'B',2:'C',3:'D',4:'E',5:'F',6:'G',7:'H',8:'I',9:'J',10:'K',11:'L',12:'M',13:'N',14:'O',15:'P',16:'Q',17:'R',18:'S',19:'T',20:'U',21:'V',22:'W',23:'X',24:'Y',25:'Z',26:'DEL',27:'NOTHING',28:'SPACE',30:'OTHER'}
            result=label[np.argmax(prediction)]
            print(result)
            return str(result)
        elif(id==2):
            loaded_model = keras.models.load_model('modelisl.h5')
            prediction=loaded_model.predict(img)[0]
            print(prediction)
            label={0:'A',1:'B',2:'C',3:'D',4:'E',5:'F',6:'G',7:'H',8:'I',9:'J',10:'K',11:'L',12:'M',13:'N',14:'O',15:'P',16:'Q',17:'R',18:'S',19:'T',20:'U',21:'V',22:'W',23:'X',24:'Y',25:'Z',26:'DEL',27:'NOTHING',28:'SPACE',30:'OTHER'}
            result=label[np.argmax(prediction)]
            print(result)
            return str(result)
        else:
            return str('post received')
    else:
        return str('get recieved')

@app.route('/input/<int:id>', methods = ['GET'])
def testing(id):
    return str(id)

#app.run(__name__)

app.run(host='192.168.137.1',port=80, debug=True,threaded=False)
