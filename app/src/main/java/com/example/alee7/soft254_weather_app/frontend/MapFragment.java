package com.example.alee7.soft254_weather_app.frontend;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alee7.soft254_weather_app.R;
import com.example.alee7.soft254_weather_app.enumerator.WeatherType;
import com.example.alee7.soft254_weather_app.enumerator.WindDirection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Context context;

    FirebaseFirestore fbData = FirebaseFirestore.getInstance();
    CollectionReference dbRef = fbData.collection("weather-info");

    CollectionReference collectionReference = fbData.collection("doc-name");

    private static double ln,lt;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView)mView.findViewById(R.id.map);

        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().setTitle(R.string.mapTitle);
        context = getActivity();
        return mView;
    }


    HashMap<String,String> names = new HashMap<String,String>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        String lastDataPulled = "";
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.i("Test: ", "Filling document name list with returned data names: " + document.getId() + ", " + document.getData().toString());
                        names.put(document.getId(),(String)document.get("doc-id"));
                    }
                    OnDocPullSuccess();
                }
            }
        });



       /*dbRef.whereLessThanOrEqualTo("postTime", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(QueryDocumentSnapshot document: task.getResult()){
                       CreateMarker(mGoogleMap,(HashMap<String, Object>) document.getData());
                   }
               }
           }
       });*/


        CameraPosition statLib = CameraPosition.builder().target(new LatLng(lt, ln)).zoom(16).tilt(45).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(statLib));
    }

    private void OnDocPullSuccess(){
        ArrayList<String> allDocs = new ArrayList<String>();

        Log.i("Test: ", "Testing data in names hashmap, isEmpty(): " + names.isEmpty());

        for(Map.Entry<String, String> name: names.entrySet()){
            Log.i("Test: ", "Adding name to document list: " + name.getValue());
            allDocs.add(name.getValue());
        }

        for(String docName: allDocs) {
            DocumentReference newMarker = dbRef.document(docName);

            newMarker.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                        CreateMarker(mGoogleMap,(HashMap<String, Object>) task.getResult().getData());
                    else
                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            });
            /*
            Task<DocumentSnapshot> task = dbRef.document(docName).get();
            if (task.isSuccessful()) {
                Log.i("Test: ", "Document Retrieval successful @ document: " + docName);
                DocumentSnapshot pulledData = task.getResult();
                Log.i("Test: ", "Document data = " + pulledData.getData().toString());
                if(pulledData != null){
                    CreateMarker(mGoogleMap,(HashMap<String, Object>) pulledData.getData());
                }
            } else{
                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
            }
            */
        }
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public void CreateMarker(GoogleMap googleMap, HashMap<String,Object> recordItem) {

        GeoPoint geoPoint = (GeoPoint) recordItem.get("location");

        String address = GetAddressFromLocation(geoPoint);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())).title(address).snippet(
                "Weather Type: " + WeatherType.getEnumByIndex(safeLongToInt((long)recordItem.get("weather-type"))).toString() +
                        "\nFeels like: " + recordItem.get("user-temp") + "°C" +
                        "\nWind Direction: " + WindDirection.getEnumByIndex(safeLongToInt((long)recordItem.get("wind-direction"))).toString() +
                        "\nWind Speed: " + recordItem.get("wind-speed") + "mph" +
                        "\nLocal Pressure: " + recordItem.get("pressure") +
                        "\nTime Recorded: " + recordItem.get("postTime")));

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public String GetAddressFromLocation(GeoPoint geoPoint){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(geoPoint.getLatitude(),geoPoint.getLongitude(), 1);

            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                //  String city = addresses.get(0).getLocality();
                //  String state = addresses.get(0).getAdminArea();
                // String country = addresses.get(0).getCountryName();
                // String postalCode = addresses.get(0).getPostalCode();
                return (address);
            }
            else {
                return (getString(R.string.No_Address));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return(getString(R.string.Cannot_Get_Address));
        }
    }

    public static void SetCurentLocation(double lat, double lon){
        ln = lon;
        lt = lat;
    }
}
