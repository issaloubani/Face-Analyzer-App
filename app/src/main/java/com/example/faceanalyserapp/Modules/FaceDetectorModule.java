package com.example.faceanalyserapp.Modules;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.facedetector.Rectangle;
import io.fotoapparat.preview.Frame;

public class FaceDetectorModule {

    private Frame currentFrame;
    private FirebaseVisionImageMetadata metadata;

    private FirebaseVisionImageMetadata initFirebaseVisionImageMetadata() {

        return new FirebaseVisionImageMetadata.Builder()
                .setWidth(this.currentFrame.getSize().width)
                .setHeight(this.currentFrame.getSize().height)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                .build();
    }

    public FaceDetectorModule(Frame frame) {
        this.currentFrame = frame;

    }

    public List<Rectangle> detectFaces() {
        this.metadata = initFirebaseVisionImageMetadata();
        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(this.currentFrame.getImage(), metadata);
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        List<Rectangle> rectangles = new ArrayList<>();

        detector.detectInImage(image)
                .addOnSuccessListener(faces -> {
                    for (FirebaseVisionFace face : faces) {
                        List<FirebaseVisionPoint> faceContours = face.getContour(FirebaseVisionFaceContour.FACE).getPoints();

                        for (int i = 0; i < faceContours.size(); i++) {

                            if (i + 1 != faceContours.size())
                                rectangles.add(new Rectangle(faceContours.get(i).getX(),
                                        faceContours.get(i).getY(),
                                        faceContours.get(i + 1).getX(), faceContours.get(i + 1).getY()));

                        }
                    }

                })
                .addOnFailureListener(e -> {


                });


        return rectangles;
    }

}
