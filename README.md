# android-coroutine-scopes

This very basic project illustrates the differences between commonly used Coroutine scopes in Android development.  The following scopes will be used:

* GlobalScope - dispatches onto IO thread, very difficult to cancel leading to memory leaks!
* CoroutineScope(Dispatchers.IO) - dispatches onto IO thread, and also memory leaky!
* lifecycleScope - dispatches onto main thread, but main thread safe if calling suspend function, memory leak proof!
* viewModelScope - similar to lifecycleScope but run in a ViewModel, observe the result in Activity using LiveData!

The sample should showcase that the ideal solution is to use a combination of viewModelScope and LiveData.  This gives us the Android built-in support
for coroutine cancellation when the ViewModel onCleared function is called which prevents leaking the coroutine.  And combined with LiveData
we have a perfect way to observe and display the result of the coroutine function in the Activity/Fragment on the main thread in a Lifecycle friendly manner.

## Glossary
* Scope - reference to a context, used by the coroutine builder (launch, async etc) to figure out the coroutine context
* Context - a map of elements that describes the environment of a coroutine for example Job, Dispatcher, and coroutine name
* Job - responsible for coroutine’s lifecycle, cancellation, and parent-child relations
* launch - coroutine builder which launches a coroutine in a scope
