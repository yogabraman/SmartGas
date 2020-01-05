var functions = require('firebase-functions');
var admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.kadarNotification = functions.database.ref('SmartGas/{Alat}/Gas')
        .onUpdate((change, context) => {
  		
  		const eventSnapshot = change.after;
        
        const topic = "kadar"+context.params.Alat;
        const kadar = eventSnapshot.child("kadar").val().toString();
  		const payload = {
          data: {
                title: "Gas LPG Bocor",
            	body: "Tenang, Smart Gas sudah menangani"
          }
        };
  		
  		if(kadar >= 1.8){
         	// Send a message to devices subscribed to the provided topic.
            return admin.messaging().sendToTopic(topic, payload)
        }
            
        });

exports.lpgNotification = functions.database.ref('SmartGas/{Alat}/Regulator')
        .onUpdate((change, context) => {
  		
  		const eventSnapshot = change.after;
        
        const topic = "lpg"+context.params.Alat;
        const value = eventSnapshot.child("value").val().toString();
  		const payload = {
          data: {
            	title: "Gas Hampir Habis",
            	body: "Segera isi ulang gas LPG"
          }
        };
  		
  		if(value <= 10){
         	// Send a message to devices subscribed to the provided topic.
            return admin.messaging().sendToTopic(topic, payload)
        }
            
        });

exports.gantiLPG = functions.database.ref('SmartGas/{Alat}/Grafik/{tahun}/{bulan}/{grafik}')
        .onWrite((change, context) => {
        const eventSnapshot = change.after;

        const alat = context.params.Alat;
  		const uid = context.params.grafik;
        const firstValue = eventSnapshot.child("firstValue").val();
  		const lastValue = eventSnapshot.child("lastValue").val();
        
  		if(firstValue >= 98){
            admin.database().ref("SmartGas/"+alat+"/AI/AI-data").remove();
          	// Set Data on new child.
            admin.database().ref("SmartGas/"+alat+"/AI/AI-data").push().set({
                     selisih: firstValue-lastValue
                 });
         	// Set Data on new child.
             return admin.database().ref("SmartGas/"+alat+"/GantiLPG").push().set({
                     waktu: admin.database.ServerValue.TIMESTAMP
                 });
        }
        });

exports.warningNotification = functions.database.ref('SmartGas/{Alat}/Grafik/{tahun}/{bulan}/{grafik}')
        .onWrite((change, context) => {
  		
  		const eventSnapshot = change.after;
        
        const topic = "warning"+context.params.Alat;
        const alat = context.params.Alat;
        const firstValue = eventSnapshot.child("firstValue").val();
  		const lastValue = eventSnapshot.child("lastValue").val();
        
  		const payload = {
          data: {
                title: "Penggunaan tidak wajar!!",
            	body: "Penggunaan gas Anda diatas rata-rata. \n\nMatikan Regulator Sekarang?"
          }
        };
        admin.database().ref("Smartgas/"+alat+"/AI").ref.once("value")
        .then(function(snapshot) {
            const average = snapshot.child("average").val();
            
            if(firstValue-lastValue >= 2*average){
                // Send a message to devices subscribed to the provided topic.
               return admin.messaging().sendToTopic(topic, payload)
           }

        });
 
        });