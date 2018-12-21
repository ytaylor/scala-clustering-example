# scala-clustering-example
 Using the data of Gesture data and the k-means method search a segmentation or classification in groups of the frames of a video.
 
## Exploring the data
This data set collects several features extracted from 7 videos with people gesticulating, with the aim of studying the segmentation of the gestures phase. Each video is represented by two files: a raw file (raw), containing the hands position,
dolls, head and spine of the user in each frame. And a processed file, which contains speed and acceleration of hands and wrists. 

## Preparation of the data
Taking into account the characteristics of the data set, it is necessary to put together the two files of
characteristics in a single data set, this preparation has been done manually in
Excel In the case of the a1 raw file that talks about speed and acceleration, they are calculated with a
displacement of 3 frames. That means that the first 4 frames of the raw do not have the
processed characteristics and therefore it is necessary to eliminate them. 

But the classification of the data of the original set has been carried out to then verify if the
The results obtained are similar to those obtained with the K-means algorithm.

```
val parsedData = data.map(x => {
val parts = x.split(",") 
val species = parts(50) match {
case "Rest" => 1.0
case "Preparation" => 2.0
case "Stroke" => 3.0
case "Hold" => 4.0
case "Retraction" => 5.0
}
parts.dropRight(1).map(_.toDouble)++Array(species)
})
```
## Clustering
To perform the clustering with the K-means method, as the cluster number is not known, in
first instance an algorithm is applied that determines which is the most appropriate number of clusters,
taking into account the cost of the algorithm for each K. Generally WSSSE decreases when k
increases, and the value of k is taken from which the variations are not too large.

From this step we have obtained different WSSE that have been decreasing with the increase in the value of
K, the figure below shows the result obtained for each of the k and its
graphic representation. As shown in the graph, I have considered that the appropriate value for k
it can be considered 5, because it is where the variations begin to be smaller.


