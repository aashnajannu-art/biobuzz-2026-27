{
  "startPoint": {
    "x": 56,
    "y": 8,
    "locked": false,
    "headingDeg": 270
  },
  "lines": [
    {
      "id": "line-redup-1",
      "color": "#ffc516",
      "name": "Shoot preload -> garden",
      "locked": false,
      "waitBeforeMs": 1500,
      "waitAfterMs": 0,
      "waitBeforeName": "Shoot preload",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 28,
        "y": 12
      },
      "controlPoints": [
        {
          "x": 45,
          "y": 25
        }
      ],
      "heading": {
        "type": "linear",
        "startDeg": 270,
        "endDeg": 180
      }
    },
    {
      "id": "line-redup-2",
      "color": "#22c55e",
      "name": "Intake garden (4 balls)",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 10,
        "y": 9
      },
      "controlPoints": [],
      "heading": {
        "type": "constant",
        "degrees": 180
      }
    },
    {
      "id": "line-redup-3",
      "color": "#a855f7",
      "name": "Garden -> start (shoot)",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 56,
        "y": 8
      },
      "controlPoints": [
        {
          "x": 40,
          "y": 25
        }
      ],
      "heading": {
        "type": "linear",
        "startDeg": 180,
        "endDeg": 270
      }
    },
    {
      "id": "line-redup-4",
      "color": "#ef4444",
      "name": "Park in red box",
      "locked": false,
      "waitBeforeMs": 1500,
      "waitAfterMs": 0,
      "waitBeforeName": "Shoot 2",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 9,
        "y": 106
      },
      "controlPoints": [
        {
          "x": 18,
          "y": 30
        }
      ],
      "heading": {
        "type": "constant",
        "degrees": 270
      }
    }
  ],
  "shapes": [],
  "sequence": [
    {
      "kind": "path",
      "lineId": "line-redup-1"
    },
    {
      "kind": "path",
      "lineId": "line-redup-2"
    },
    {
      "kind": "path",
      "lineId": "line-redup-3"
    },
    {
      "kind": "path",
      "lineId": "line-redup-4"
    }
  ]
}
