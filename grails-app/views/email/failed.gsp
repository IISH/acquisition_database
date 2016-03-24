<%@ defaultCodec="none" %>The process '${digitalMaterialStatus}' of the collection with PID ${digitalMaterialStatus.collection.objectRepositoryPID} and the name '${digitalMaterialStatus.collection.name}' has failed!

Link to collection in the acquisition database: <g:createLink controller="collection" action="edit" id="${digitalMaterialStatus.collection.id}" absolute="${true}"/>

The process failed at <g:formatDate date="${digitalMaterialStatus.lastStatusChange}" formatName="default.datetime.format"/> <g:if test="${digitalMaterialStatus.message}">with the message: ${digitalMaterialStatus.message}</g:if>.

---
This email was automatically sent by the Aquisition database