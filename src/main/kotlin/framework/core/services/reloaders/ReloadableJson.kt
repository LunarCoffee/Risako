package framework.core.services.reloaders

// Wrapper class for a JSON string that can be deserialized to a class which extends [Reloadable].
// This is used so those classes can have arbitrary members while still being under one class (and
// therefore universally reloadable).
class ReloadableJson(val json: String, val id: Long)
