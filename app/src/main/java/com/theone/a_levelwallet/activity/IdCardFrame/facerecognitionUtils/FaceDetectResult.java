package com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils;

import java.util.List;

//�������������
public class FaceDetectResult {
	private List<FaceModel> facemodels;

	public List<FaceModel> getFacemodels() {
		return facemodels;
	}

	public void setFacemodels(List<FaceModel> facemodels) {
		this.facemodels = facemodels;
	}

	@Override
	public String toString() {
		return "FaceDetectResult [facemodels=" + facemodels + "]";
	}

}
