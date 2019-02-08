#!/usr/bin/env okscript

import com.baulsupp.okurl.kotlin.jsonPostRequest
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.services.dropbox.model.DropboxFileList

val path = args.firstOrNull() ?: ""
val files = query<DropboxFileList>(jsonPostRequest("https://api.dropboxapi.com/2/files/list_folder", "{\"path\": \"$path\"}"))

for (file in files.entries) {
  println("%-25s %-10d".format(file.name, file.size))
}
