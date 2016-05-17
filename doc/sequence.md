@startuml
actor Admin
actor ProjectLeader
actor GitHub
control EndPoint
control SeedService
database ProjectSeedCacheDescriptor

== Project generation ==

Admin -> Seed:Project parameters
Seed -> ProjectGenerationStep:Runs
ProjectGenerationStep -> ProjectGenerationStep:Generates project folder
ProjectGenerationStep -> ProjectSeed: Creates
activate ProjectSeed
ProjectGenerationStep -> ProjectSeedCacheDescriptor:Stores project seed path

== Branch generation ==

ProjectLeader -> ProjectSeed:Branch parameters
ProjectSeed -> BranchGenerationStep:Runs
BranchGenerationStep -> BranchGenerationStep:Creates branch folder
BranchGenerationStep -> BranchSeed:Creates
activate BranchSeed
BranchGenerationStep -> BranchSeed:Runs

== Branch pipeline generation ==

ProjectLeader -> BranchSeed: Runs
BranchSeed -> BranchPipeline: Create
activate BranchPipeline

== Create trigger ==

GitHub -> EndPoint: Send Create event
EndPoint -> SeedService: Create(project, branch)
SeedService -> ProjectSeedCacheDescriptor: Gets project config
ProjectSeedCacheDescriptor -> ProjectSeed: Gets project config
ProjectSeed --> ProjectSeedCacheDescriptor: config
ProjectSeedCacheDescriptor --> SeedService: config
SeedService -> ProjectSeed: Branch parameters

== Seed trigger ==

GitHub -> EndPoint: Send Create event
EndPoint -> SeedService: Seed(project, branch)
SeedService -> ProjectSeedCacheDescriptor: Gets project config
ProjectSeedCacheDescriptor -> ProjectSeed: Gets project config
ProjectSeed --> ProjectSeedCacheDescriptor: config
ProjectSeedCacheDescriptor --> SeedService: config
SeedService -> BranchSeed: Branch parameters

== TODO Commit trigger ==
== TODO Delete trigger ==

@enduml