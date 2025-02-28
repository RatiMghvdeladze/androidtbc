package com.example.androidtbc.presentation.location

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import com.example.androidtbc.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class ClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<LocationClusterItem>
) : DefaultClusterRenderer<LocationClusterItem>(context, map, clusterManager) {

    private val iconGenerator = IconGenerator(context)
    private val iconTextView: TextView

    init {
        val multiProfile = LayoutInflater.from(context).inflate(R.layout.cluster_icon, null)
        iconTextView = multiProfile.findViewById(R.id.cluster_text)
        iconGenerator.setContentView(multiProfile)
        iconGenerator.setBackground(null)
    }

    override fun onBeforeClusterItemRendered(
        item: LocationClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.getTitle())
            .snippet(item.getSnippet())
    }


    override fun onBeforeClusterRendered(
        cluster: Cluster<LocationClusterItem>,
        markerOptions: MarkerOptions
    ) {
        val clusterSize = cluster.size.toString()
        iconTextView.text = clusterSize

        when {
            cluster.size < 10 -> iconTextView.setTextColor(Color.GREEN)
            cluster.size < 20 -> iconTextView.setTextColor(Color.YELLOW)
            else -> iconTextView.setTextColor(Color.RED)
        }

        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<LocationClusterItem>): Boolean {
        return cluster.size > 1
    }
}
