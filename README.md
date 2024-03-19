TMDB Compose
========

An Android native demo project built using the MVI architecture with Jetpack Compose platform. It uses the TMDB APIs as its source of data.

Screens
========

Movies List
--------

A screen listing top rated movies. The screen contains a search bar to enable users to search among them.

Details
--------

A screen displaying the selected movie details such as the title, a small description, when it was released and its rate.

Architecture
========

The project is divided into modules each containing an independent layer for better separation of concerns and maintainability.

App
--------

This is the central module, providing other modules with the DI for each other (hence why it has all other modules in its gradle).

Core
--------

It contains the common base abstractions for all layers to share. For example, the DataState and the ApiError classes used by domain layer as a wrapper for API responses and states in view models in the UI layer.

Core UI
--------

It includes the base classes for all UI components as well as the shared functionality among composables. Functionality is shared in delegate classes, created and used mainly as a means of communication between composables instead of having to pass callbacks up the composable tree.

Data
-------

Works as the first inner layer which includes API implementations, local data caching mechanisms and error handling. It comprises 3 sub modules as following:

_Remote_
-------

Has the API implementations and the HTTP client and network configs.

_Local_
-------

Has the caching mechanism implementation (Room DB in our case).

_Repo_
------

Has repos using built-in mappers and the previous 2 modules to fetch data, map it to domain models, and passes it to upper layers.

Domain
------

The next layer containing the models used by UI layer. It has use cases injected into view models that works as a middle man between repos and view models.

UI
------

The last layer that has UI elements implementation and view models. View models control composables using delegates, states and actions. The base view model in Core UI module has a delegate containing all basic states needed for a screen (isLoading, hasError, etc.). It also includes one used to keep track of all the related pagination details.

Data Flow
======

The data flow is the life cycle of data requests. Here is a simple graph showing what happens:
                                                                                                            
                                                                                                    fetches data based on request implementation
                requesting data                starts a new flow              forwards to repo      (whether it is cachable and has data or not)
            UI =================> view model ====================> use case ===================> repo ==========================================> remote OR local
            UI <================= view model <==================== use case <=================== repo <========================================== remote OR local
              updates a single state        forwards dataState flow       maps data/error and wraps     returns raw API/DB response through flow
            flow with the final result                                     it in DataState object

It is worth mentioning that the base view model has a single state flow that is filtered in composables according to the states each needs. This state flow is updated from multiple flows (one flow per request). A cleaner way is to have a single source request flow that is mapped to states so this is a future plan.

For caching mechanism, the desicion was to cache only 1 page (the last page acquired by user) to save storage and simplify the demo. Caching is not applied in search flow for the same reason.

Regarding the search feature, it is missing the debounce operator to reduce redundant API calls and unnecessary data processing. This is due to not having a single request flow and should be doable once I have it.

Git strategy
-------

each feature has its own branch. Once the feature is done, a PR is made. After approval, PR is rebased onto dev rather than merged for cleaner git history. Also, all commits are squashed into a single commit before rebasing, creating one big commit in each branch.
