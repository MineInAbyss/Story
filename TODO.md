# TODO

### Important
- [x] Remove tests in favor of documentation
- [x] Remove development hack, make datakey's javaplugin non-nullable
- [x] Check lazy init to not load&save on disable
- [x] Caching for items
- [ ] Update TODO and Documentation (Especially for items etc.)

### Additions
- [ ] Properly implement Java MySQL Endpoint !!!
- [ ] Maybe implement batching
- [ ] Maybe remove data on entity death to clear caches prematurely etc.
- [ ] Removing a world won't clean up the worlds data
- [ ] Make kotlin functions inline (InstanceAccess done)

### Improvements
- [x] Adjust cache sizes for all public data stores (2/2)
- [ ] Add config for caches (1/2)
- [x] Maybe move from paper to spigot
- [ ] Improve Exposed MySQL Endpoint
- [ ] Only clean cache after tick
- [ ] Try other caching libraries like caffeine or cache2k
