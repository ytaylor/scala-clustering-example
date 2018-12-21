/*
*  Clasificación (no supervisada) de frames de un vídeo
*/

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.clustering.KMeansModel

//Importando el archivo a1_va3. Eliminado el encabezado de los datos
val data = sc.textFile("allData.csv")


//Importando el archivo a1_raw. Eliminado el encabezado de los datos y las tres primeras lineas
val data1 = sc.textFile("a1_raw.csv")
val filtData = data1.filter(line => !line.contains("lhx,lhy,lhz,rhx,rhy,rhz,hx,hy,hz,sx,sy,sz,lwx,lwy,lwz,rwx,rwy,rwz,timestamp,phase"))
val filteredRdd2 = filtData.zipWithIndex().collect { case (r, i) if i != 0 => r }
val filteredRdd3 = filteredRdd2.zipWithIndex().collect { case (r, i) if i != 0 => r }
val filteredRdd4 = filteredRdd3.zipWithIndex().collect { case (r, i) if i != 0 => r }
val filteredRdd5 = filteredRdd4.zipWithIndex().collect { case (r, i) if i != 0 => r }

//Viendo cuantos hay de cada tipo 
val user_arrays= filteredRdd5.map(line => line.split(","))
val user_fields= user_arrays.map(userRecord=> (userRecord(0),  userRecord(1),  userRecord(2), userRecord(3), userRecord(4), userRecord(5), userRecord(6),userRecord(7),userRecord(8),userRecord(9),userRecord(10),userRecord(11),userRecord(12),userRecord(13),userRecord(14),userRecord(15),userRecord(16),userRecord(17),userRecord(18),userRecord(19)))
val num_rest= user_fields.map(tupla=> tupla._20).distinct().count()
val count_by_ages= user_fields.map{ case (ids, ids2, ids3, ids4, ids5,ids6,ids7, ids8, ids9, ids10, ids11, ids12, ids13, ids14, ids15, ids16, ids17, ids18, ids19, ids20) => (ids20, 1)}.reduceByKey(_+_).collect()


//Convirtiendo los datos obtenidos en RDD con sus valores en double y guardando la clase(para luego comparar)
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

//Convertir sos datos en vectores
val parsedDataV = parsedData.map(x => Vectors.dense(x.slice(0,50))).cache()

//Buscando el numero de clusters k
val numIterations = 20

for(k<-Array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)){
  val model = KMeans.train(parsedDataV, k, numIterations)
  val WSSSE = model.computeCost(parsedDataV)
  println(s"k = ${k} \t-> WSSSE(k) = ${WSSSE}")
  }

//Se ha determinado que el numero de clusters mas adecuado teniendo en cuenta el WSSSE es 5
val numClusters = 5
val numIterations = 20
val model = KMeans.train(parsedDataV, numClusters, numIterations)

// métrica de evaluación
val WSSSE = model.computeCost(parsedDataV)
println("Within Set Sum of Squared Errors = " + WSSSE)

///haciedo la predccion del modelo
val predictions = model.predict(parsedDataV)

//Viendo la cantidad de clusters de la prediccion
val clusterCount = predictions.countByValue
clusterCount.toList.foreach{ println }
model.clusterCenters.foreach(println)

println(predictions.take(10).mkString(","))
