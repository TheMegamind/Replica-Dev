def playFavoriteById(favoriteId) {
    selectedFavorite = state.favorites.find {it.id==favoriteId}
    favoriteName = selectedFavorite.name
    sendCommand("playFavorite",favoriteId)
    logInfo "${device.displayName} playing favorite by ID: $favoriteId | $favoriteName}"  
    date = new Date()
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    requestDate = sdf.format(date)   
    lastFavoriteRequest = JsonOutput.toJson([ Id: favoriteId, Name: favoriteName, Time: requestDate ])   
    sendEvent(name: "lastFavoriteRequest", value: lastFavoriteRequest)
}
        
def playFavoriteByName(favoriteName) {
    selectedFavorite = state.favorites.find {it.name==favoriteName}  
    favoriteId = selectedFavorite.id
    sendCommand("playFavorite",selectedFavorite.id)
    logInfo"${device.displayName} playing favorite by Name: ID: $selectedFavorite.id | $selectedFavorite.name"
    date = new Date()
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    requestDate = sdf.format(date)   
    lastFavoriteRequest = JsonOutput.toJson([ Id: favoriteId, Name: favoriteName, Time: requestDate ])   
    sendEvent(name: "lastFavoriteRequest", value: lastFavoriteRequest)
}

def playRandomFavorite() {
    Random rnd = new Random()
    selectedFavorite = (rnd.nextInt(state.favorites.size))
    favoriteId = state.favorites[selectedFavorite].id
    favoriteName = state.favorites[selectedFavorite].name
    sendCommand("playFavorite",favoriteId)  
    logInfo "${device.displayName} playing random favorite ID: $favoriteId | $favoriteName}" 
    date = new Date()
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    requestDate = sdf.format(date)   
    lastFavoriteRequest = JsonOutput.toJson([ Id: favoriteId, Name: favoriteName, Time: requestDate ])   
    sendEvent(name: "lastFavoriteRequest", value: lastFavoriteRequest)
}
