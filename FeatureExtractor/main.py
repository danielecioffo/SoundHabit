import pandas as pd
import os
import csv
import Extractor

ORIGINAL_DATASET_PATH = "genres/"
# genres = 'blues classical country disco hiphop jazz metal pop reggae rock'.split()
genres = 'blues classical jazz metal pop rock'.split()


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
            to_append = Extractor.extract_feature(song_name)
            to_append += f' {g}'  # finally the genre is inserted

            # Insert the new calculated values of this song in the file
            file = open('data.csv', 'a', newline='')
            with file:
                writer = csv.writer(file)
                writer.writerow(to_append.split())


def show_dataset():
    data = pd.read_csv('data.csv')
    print(data.head())


if __name__ == '__main__':
    show_dataset()
