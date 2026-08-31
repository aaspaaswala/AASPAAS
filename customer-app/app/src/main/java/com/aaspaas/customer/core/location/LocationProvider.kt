package com.aaspaas.customer.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class UserLocation(val latitude: Double, val longitude: Double)

interface LocationProvider {
    suspend fun getCurrentLocation(): Result<UserLocation>
}

@Singleton
class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<UserLocation> =
        suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        cont.resume(Result.success(UserLocation(location.latitude, location.longitude)))
                    } else {
                        cont.resume(Result.failure(IllegalStateException("Location unavailable")))
                    }
                }
                .addOnFailureListener { cont.resumeWithException(it) }
            cont.invokeOnCancellation { cts.cancel() }
        }
}
