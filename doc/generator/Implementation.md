Generator implementation
========================

In order to get the full benefit of the Jenkins Job DSL and its capabilities of dealing with unmanaged jobs and views,
 the plug-in must be used directly and not through calling the DSL directory.

This is mostly for the pipeline generator step itself. For project seed and branch seed generation, this does not 
 matter since the management will be done either manually or through the Seed connectors.
