#Vaptor Backend Component

##User Preference Service

It is part of Master Thesis "Adaptive Video Techniques for Informal Learning Support in Workplace Environments" at Chair of Computer Science 5, RWTH-Aachen.

User preference service provides an interface to the user for entering and updating their preferences regarding the video adaptation. It will manage an extended database table corresponding to each OIDC user. During the adaptation process, this service is requested for user preferences by adapter service, which reflects in the resulting adaptive video. User preference service maintains two tables, one keeps the record of preferences like preferred duration, language, location. The other one saves user's domain of expertise and their level in that particular domain.

Compile using ant, with "ant all". Then run bin/start_network.bat for windows or bin/start_network.sh for linux.