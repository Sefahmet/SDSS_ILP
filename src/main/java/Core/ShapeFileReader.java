package Core;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.filter.identity.FeatureId;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;



public class ShapeFileReader {
    public static int getFeatureCount(String FileName){
        File shapeFile = new File(FileName);
        try {
            Map<String, Object> connect = new HashMap();
            connect.put("url", shapeFile.toURI().toString());

            DataStore dataStore = DataStoreFinder.getDataStore(connect);
            String[] typeNames = dataStore.getTypeNames();
            String typeName = typeNames[0];
            System.out.println("conting buildings " + typeName);

            FeatureSource featureSource = dataStore.getFeatureSource(typeName);
            FeatureCollection collection = featureSource.getFeatures();
            FeatureIterator iterator = collection.features();


            try {
                int i = 0;
                while (iterator.hasNext()) {
                    i++;
                }
                return i;
            } finally {
                iterator.close();
            }

        } catch (Throwable e) {}
        throw new NullPointerException();
    }
    public static FeatureId[][] readBuildingsAndPopulation(String FileName, String Property) throws FileNotFoundException {
        File shapeFile = new File(FileName);
        if(shapeFile.exists()){
            FeatureId[][] buildingCapacities = new FeatureId[getFeatureCount(FileName)][2];
            try {
                Map<String, Object> connect = new HashMap();
                connect.put("url", shapeFile.toURI().toString());

                DataStore dataStore = DataStoreFinder.getDataStore(connect);
                String[] typeNames = dataStore.getTypeNames();
                String typeName = typeNames[0];
                System.out.println("Reading content " + typeName);

                FeatureSource featureSource = dataStore.getFeatureSource(typeName);
                FeatureCollection collection = featureSource.getFeatures();
                FeatureIterator iterator = collection.features();


                try {
                    int i = 0;
                    while (iterator.hasNext()) {
                        Feature feature = iterator.next();
                        GeometryAttribute sourceGeometry = feature.getDefaultGeometryProperty();
                        FeatureId id = feature.getIdentifier();
                        buildingCapacities[i][0] = id;
                        buildingCapacities[i][1] = (FeatureId) feature.getProperty("population").getValue();
                    }
                    return buildingCapacities;
                } finally {
                    iterator.close();
                }

            } catch (Throwable e) {}

    }
    else{
    throw new FileNotFoundException(FileName+" Can Not Found");
        }
        return null;
    }

}
