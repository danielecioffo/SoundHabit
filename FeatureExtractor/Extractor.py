import librosa
import numpy as np


def extract_feature(path):
    """
        Function used to extract the necessary features from the audio signals (.wav)
        :return a string that contains all the values (to split up)
    """
    # y is the floating point time series of the audio, sr is the sampling rate
    y, sr = librosa.load(path, mono=True)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
    # Compute root-mean-square (RMS) energy for each frame
    rms = librosa.feature.rms(y=y)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
    rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zcr = librosa.feature.zero_crossing_rate(y=y)
    mfcc = librosa.feature.mfcc(y=y, sr=sr)
    to_append = f'{np.mean(chroma_stft)} {np.mean(rms)} {np.mean(spec_cent)} {np.mean(spec_bw)} {np.mean(rolloff)} ' \
                f'{np.mean(zcr)}'
    for e in mfcc:  # for each mfcc
        to_append += f' {np.mean(e)}'
    return to_append
