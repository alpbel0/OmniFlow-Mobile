package com.omniflow.data.local

/**
 * Mock şehir → koordinat tablosu.
 *
 * Backend B0.12 (geocoding) gelene kadar destinasyon pinleri için kullanılır.
 * B0.12 geldiğinde `TripDestinationDto` lat/lng döndürmeye başlayınca bu tablo
 * kaldırılır ve mapper doğrudan dto'dan beslenir — UI tarafında hiçbir değişiklik
 * gerekmez.
 */
object MockCityCoordinates {
    private val known = mapOf(
        "istanbul" to (41.0082 to 28.9784),
        "roma" to (41.9028 to 12.4964),
        "rome" to (41.9028 to 12.4964),
        "floransa" to (43.7696 to 11.2558),
        "florence" to (43.7696 to 11.2558),
        "barselona" to (41.3851 to 2.1734),
        "barcelona" to (41.3851 to 2.1734),
        "madrid" to (40.4168 to -3.7038),
        "tokyo" to (35.6762 to 139.6503),
        "kyoto" to (35.0116 to 135.7681),
        "osaka" to (34.6937 to 135.5023),
        "napoli" to (40.8518 to 14.2681),
        "naples" to (40.8518 to 14.2681),
        "positano" to (40.6280 to 14.4849),
        "amalfi" to (40.6340 to 14.6027),
        "edinburgh" to (55.9533 to -3.1883),
        "inverness" to (57.4778 to -4.2247),
        "skye" to (57.2720 to -6.2153),
        "venedik" to (45.4408 to 12.3155),
        "venice" to (45.4408 to 12.3155),
        "oslo" to (59.9139 to 10.7522),
        "paris" to (48.8566 to 2.3522),
        "london" to (51.5074 to -0.1278),
        "berlin" to (52.5200 to 13.4050),
        "amsterdam" to (52.3676 to 4.9041),
        "viyana" to (48.2082 to 16.3738),
        "vienna" to (48.2082 to 16.3738),
        "prag" to (50.0755 to 14.4378),
        "budapeşte" to (47.4979 to 19.0402),
        "budapest" to (47.4979 to 19.0402),
        "atina" to (37.9838 to 23.7275),
        "athens" to (37.9838 to 23.7275),
        "lizbon" to (38.7223 to -9.1393),
        "lisbon" to (38.7223 to -9.1393),
        "dublin" to (53.3498 to -6.2603),
        "stockholm" to (59.3293 to 18.0686),
        "kopenhag" to (55.6761 to 12.5683),
        "copenhagen" to (55.6761 to 12.5683),
        "helsinki" to (60.1699 to 24.9384),
        "reykjavik" to (64.1466 to -21.9426),
        "new york" to (40.7128 to -74.0060),
    )

    /**
     * @return eşleşen (latitude, longitude) çifti; bilinmeyen şehir için `null`
     *         (spec: koordinatı null olan destinasyon pin/rota çiziminden atlanır).
     */
    fun lookup(cityName: String): Pair<Double, Double>? =
        known[cityName.trim().lowercase()]
}