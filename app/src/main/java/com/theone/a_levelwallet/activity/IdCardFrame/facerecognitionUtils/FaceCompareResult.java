package com.theone.a_levelwallet.activity.IdCardFrame.facerecognitionUtils;

public class FaceCompareResult {
	private float similar;

	public float getSimilar() {
		return similar;
	}

	public void setSimilar(float similar) {
		this.similar = similar;
	}

	@Override
	public String toString() {
		return "FaceDetectResult [similar=" + similar + "]";
	}
	

}
