package engine.model

class SourcesModel {
    var sources = mutableSetOf<String>()

    fun addSource(filePath: String) {
        sources.add(filePath)
    }

    fun addSources(filePaths: Set<String>) {
        sources.addAll(filePaths)
    }

    fun removeSource(filePath: String) {
        sources.remove(filePath)
    }
}