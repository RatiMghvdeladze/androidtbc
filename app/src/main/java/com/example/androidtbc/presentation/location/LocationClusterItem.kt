package com.example.androidtbc.presentation.location

import com.example.androidtbc.presentation.location.model.LocationUi
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class LocationClusterItem(
    val locationUi: LocationUi
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(locationUi.latitude, locationUi.longitude)
    override fun getTitle(): String = locationUi.title
    override fun getSnippet(): String = locationUi.address
}