import librosa
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import os
import pathlib
import csv
from sklearn.preprocessing import LabelEncoder, StandardScaler

ORIGINAL_DATASET_PATH = "genres/"
genres = 'blues classical country disco hiphop jazz metal pop reggae rock'.split()


# NOT USED, but spectograms can be used to feed a Convolutional Neural Network, for better performance
def extract_spectrograms_from_original_dataset():
    """
    Function used for create all the spectrograms
    """
    cmap = plt.get_cmap('inferno')  # only for cosmetic reason
    plt.figure(figsize=(10, 10))
    for g in genres:
        pathlib.Path(f'img_data/{g}').mkdir(parents=True, exist_ok=True)
        for filename in os.listdir(ORIGINAL_DATASET_PATH + f'{g}'):
            song_name = ORIGINAL_DATASET_PATH + f'{g}/{filename}'
            y, sr = librosa.load(song_name, mono=True)
            # Fs: sampling unit, Fc: center frequency
            # Fs, Fc, noverlap, sides, mode are set to the default values
            plt.specgram(y, NFFT=2048, Fs=2, Fc=0, noverlap=128, cmap=cmap, sides='default', mode='default',
                         scale='dB')
            plt.axis('off')
            plt.savefig(f'img_data/{g}/{filename[:-3].replace(".", "")}.png')
            plt.clf()


def extract_features_from_original_dataset():
    """
    Function used to extract all the possible features from the audio signals in the original dataset
    In output we will have the data.csv file, that will be our dataset
    """

    # construct the header of the table
    header = 'chroma_stft rms spectral_centroid spectral_bandwidth rolloff zero_crossing_rate'
    # There are 20 MFCC, we will call them mfcc0, mfcc1, ecc
    for i in range(1, 21):
        header += f' mfcc{i}'
    # Finally we need the "label" column
    header += ' label'
    header = header.split()

    # data.csv will be the new dataset
    file = open('data.csv', 'w', newline='')
    with file:
        writer = csv.writer(file)
        writer.writerow(header)  # Write the header
    for g in genres:
        for filename in os.listdir(ORIGINAL_DATASET_PATH + f'{g}'):
            song_name = ORIGINAL_DATASET_PATH + f'{g}/{filename}'
            # y is the floating point time series of the audio, sr is the sampling rate
            y, sr = librosa.load(song_name, mono=True, duration=30) # duration 30s, the duration of the audio clip
            # Compute a chromagram
            chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
            # Compute root-mean-square (RMS) energy for each frame
            rms = librosa.feature.rms(y=y)
            spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
            spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
            rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
            zcr = librosa.feature.zero_crossing_rate(y)
            mfcc = librosa.feature.mfcc(y=y, sr=sr)
            to_append = f'{np.mean(chroma_stft)} {np.mean(rms)} {np.mean(spec_cent)} {np.mean(spec_bw)} ' \
                        f'{np.mean(rolloff)} {np.mean(zcr)}'
            for e in mfcc:  # for each mfcc
                to_append += f' {np.mean(e)}'
            to_append += f' {g}'  # finally the genre is inserted

            # Insert the new calculated values of this song in the file
            file = open('data.csv', 'a', newline='')
            with file:
                writer = csv.writer(file)
                writer.writerow(to_append.split())


def show_dataset():
    data = pd.read_csv('data.csv')
    print(data.head())
    print("-------------------------")
    # Other operations performed in the notebook
    genre_list = data.iloc[:, -1]
    encoder = LabelEncoder()
    y = encoder.fit_transform(genre_list)
    scaler = StandardScaler()
    X = scaler.fit_transform(np.array(data.iloc[:, :-1], dtype=float))
    print(y)
    print("-------------------------")
    print(X)


if __name__ == '__main__':
    show_dataset()
