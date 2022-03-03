// import 'dart:typed_data';
// import 'dart:ui' as UI;
//
// import 'package:flutter/material.dart';
// import 'package:flutter/rendering.dart';
//
// ///
// /// description : TODO:类的作用
// /// date : 2022/02/28 11:45
// /// author : Monika
// ///
//
// class WidgetToImage extends StatefulWidget {
//   WidgetToImage({Key key}) : super(key: key);
//
//   @override
//   _WidgetToImageState createState() => _WidgetToImageState();
// }
//
// class _WidgetToImageState extends State<WidgetToImage> {
//   GlobalKey _globalKey = new GlobalKey();
//
//   @override
//   Widget build(BuildContext context) {
//     return RepaintBoundary(
//         key: _globalKey,
//         child: Column(children: [
//           Container(
//             margin: EdgeInsets.only(top: 10, bottom: 20),
//             child: Text(
//               "味多美A店会员注册",
//               style: Theme
//                   .of(context)
//                   .textTheme
//                   .headline2
//                   .copyWith(fontSize: 20, fontWeight: FontWeight.w500),
//             ),
//           ),
//           Container(
//             padding: EdgeInsets.only(
//                 left: MediaQuery
//                     .of(context)
//                     .size
//                     .width / 5,
//                 right: MediaQuery
//                     .of(context)
//                     .size
//                     .width / 5),
//             child:,
//           ),
//         ]));
//   }
//
//   Future<Uint8List> _capturePng() async {
//     try {
//       print('inside');
//       RenderRepaintBoundary boundary =
//       _globalKey.currentContext.findRenderObject();
//       UI.Image image2 = await boundary.toImage(pixelRatio: 3.0);
//       ByteData byteData =
//       await image2.toByteData(format: UI.ImageByteFormat.png);
//       Uint8List pngBytes = byteData.buffer.asUint8List();
//       return pngBytes;
//     } catch (e) {
//       return Uint8List(10);
//     }
//   }
// }
//
