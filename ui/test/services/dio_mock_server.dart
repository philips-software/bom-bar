/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:convert';

import 'package:dio/dio.dart';

class DioMockServer {
  DioMockServer(Dio dio) {
    dio.httpClientAdapter = adapter;
  }

  final adapter = _HttpAdapter();

  /// Defines [times] status responses.
  void respondStatus(int status,
      {String? statusMessage,
      Map<String, List<String>> headers = const {},
      int times = 1}) {
    adapter.enqueue(ResponseBody(Stream.empty(), status,
        headers: headers, statusMessage: statusMessage));
  }

  /// Defines [times] JSON responses returning JSON encoded [data].
  void respondJson(dynamic data,
      {int statusCode = 200, String? statusMessage, int times = 1}) {
    for (var i = 0; i < times; i++) {
      respondText(
        json.encode(data),
        statusCode: statusCode,
        statusMessage: statusMessage,
        headers: {
          Headers.contentTypeHeader: [Headers.jsonContentType]
        },
        times: times,
      );
    }
  }

  /// Defines [times] plain text responses returning [text].
  void respondText(String text,
      {int statusCode = 200,
      String? statusMessage,
      Map<String, List<String>>? headers,
      int times = 1}) {
    for (var i = 0; i < times; i++) {
      adapter.enqueue(
        ResponseBody.fromString(
          text,
          statusCode,
          statusMessage: statusMessage,
          headers: headers ??
              {
                Headers.contentTypeHeader: ['text/plain; charset=utf-8']
              },
        ),
      );
    }
  }

  /// Returns an ordered list of all performed requests.
  List<RequestOptions> get requests => adapter.requests;
}

class _HttpAdapter extends HttpClientAdapter {
  final requests = <RequestOptions>[];
  final responses = <ResponseBody>[];

  void enqueue(ResponseBody response) {
    responses.add(response);
  }

  @override
  Future<ResponseBody> fetch(RequestOptions options,
      Stream<List<int>>? requestStream, Future<dynamic>? cancelFuture) async {
    requests.add(options);
    if (requests.length > responses.length) {
      throw Exception(
          'Unexpected additional request: ${options.method} ${options.uri}');
    }
    return responses[requests.length - 1];
  }

  @override
  void close({bool force = false}) {
    // TODO: Ignore
  }
}
