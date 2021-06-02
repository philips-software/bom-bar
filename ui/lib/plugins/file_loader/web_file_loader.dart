/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:convert';
import 'dart:html'; // ignore: avoid_web_libraries_in_flutter

/// Web implementation of picking a file from the local file system.
class FileLoader {
  FileLoader();

  /// Picks a single file and uploads it as multipart POST to the indicated [Uri].
  Future<List<int>> load() async {
    final file = await _pickFile();
    return _getFileContent(file);
  }

  /// Picks file by observing change events from a file dialog element.
  Future<File> _pickFile() async {
    final uploadInput = _createFileDialog();
    final completer = Completer<File>();
    final subscription = uploadInput.onChange.listen((e) {
      uploadInput.remove();
      final files = uploadInput.files!;
      completer.complete(files.isNotEmpty ? files.first : null);
    });

    return completer.future.whenComplete(() => subscription.cancel());
  }

  /// Appends file upload element to the HTML DOM.
  FileUploadInputElement _createFileDialog() {
    final uploadInput = FileUploadInputElement()
      ..multiple = false
      ..draggable = true
      ..click();
    document.body!.append(uploadInput);
    return uploadInput;
  }

  /// Exposes selected [file] via memory to a stream.
  Future<List<int>> _getFileContent(File file) {
    final completer = Completer<List<int>>();

    final reader = FileReader();
    final subscription = reader.onLoadEnd.listen((event) {
      try {
        var data =
            Base64Decoder().convert(reader.result.toString().split(',').last);
        completer.complete(data);
      } catch (e) {
        completer.completeError(e);
      }
    });
    reader.readAsDataUrl(file);

    return completer.future.whenComplete(() => subscription.cancel());
  }
}
