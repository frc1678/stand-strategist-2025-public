# Stand Strategist Data Storage

This document describes how Stand Strategist stores data.

Stand Strategist stores data using the JSON format.
The app stores all the data in multiple files in
the `/Documents/Stand Strategist/` directory of the device.
The server doesn't need to clear the files after
competitions, as the user can clear and back them up from within the app.**

> [!NOTE]
> You may need to access the `/Documents/Stand Strategist/` directory
> at `/storage/emulated/0/Documents/Stand Strategist/`.

The following is the file structure of the `Stand Strategist` directory:

```text
Stand Strategist
├── profiles
│   └── Profile Name
│       ├── match_schedule.json
│       ├── settings.json
│       ├── team_data.json
│       └── tim_data.json
├── settings.json
└── trash
    └── 1234567890.zip
```

The remainder of this document describes the contents of each file.

## `match_schedule.json`

**The server shouldn't use this file.**

This file contains the match schedule for the competition.
This is in the same format as in other parts of the scouting
system, but isn't necessarily the same as the imported match schedule.
The app sorts the matches in the schedule by
match schedule when the user imports a schedule, and the user can edit the match schedule through the app.

The format is the same as in the rest of the system, and looks like this:

```jsonc
{
    "1": {
        "teams": [
            {
                "color": "blue",
                "number": "1234",
            },
            ...
            {
                "color": "red",
                "number": "5678",
            },
            ...
        ]
    },
    "2": {
        ...
    },
    ...
}
```

> [!NOTE]
> Match and team numbers are strings, not integers.

## `settings.json` in profiles

**The server should use parts of this file.**

This file contains the settings for the profile. The format of the file is the following:

```jsonc
{
    "alliance": "blue",
    "matchNumber": "1",
    "page": 0
}
```

> [!NOTE]
> The app may have more settings in the future. The server should ignore any settings it doesn't recognize.

### `alliance`

The alliance the user is scouting.
It's likely that the user only enters match data for this alliance, so the server
should use this to determine which data to use.

### `matchNumber`

The match last scouted by the user. The server shouldn't use this.

> [!NOTE]
> Match numbers are strings, not integers.

### `page`

The index of the page within a match that the user was last on. The server shouldn't use this.

## `team_data.json`

**The server should use this file.**

This file contains the data for each team across the entire competition.

The following is the format of this file:

```jsonc
{
    "1234": {
        "autoStrategies": "...",
        "strengths": "...",
        "weaknesses": "...",
        "notes": "..."
    },
    "5678": {
        "autoStrategies": "...",
        "strengths": "...",
        "weaknesses": "...",
        "notes": "..."
    },
    ...
}
```

Empty strings represent data values not entered by the user.
The server should use the [`alliance` setting](#alliance)
to determine which alliance the user is scouting and therefore has recorded data for.

## `tim_data.json`

**The server should use this file.**

This file contains the data for each team in each match.

The following is the format of this file:

```jsonc
{
    "1": {
        "1234": {
            "playedDefense": true,
            "defenseRating": 10,
            "drivesOverChargingStation": false,
            "movesPiecesBetweenRows": false,
            "brokenMechanism": "...",
            "notes": "..."
        },
        "5678": {
            "playedDefense": false,
            "defenseRating": 0,
            "drivesOverChargingStation": true,
            "movesPiecesBetweenRows": false,
            "brokenMechanism": "...",
            "notes": "..."
        },
        ...
    },
    "2": {
        ...
    }
}
```

Empty strings represent data values not entered by the user.
A value of `false` or `0` may also represent a value not entered by the user.
The server should use the [`alliance` setting](#alliance) to determine which alliance the user is
scouting and therefore has recorded data for.

## `settings.json` in the root directory

**The server should use parts of this file.**

This file contains global settings for the app. The format of the file is the following:

```jsonc
{
    "currentProfile": "My Profile",
    "profiles": [
        "My Profile",
        "Another Profile",
        "A Third Profile"
    ]
}
```

> [!NOTE]
> The app may have more settings in the future. The server should ignore any settings it doesn't recognize.

### `currentProfile`

The currently selected profile. The server shouldn't use this.

### `profiles`

The list of profiles that users have created in the app.
Each listed profile name should correspond to a folder in the `profiles` directory.
The server should use this list to find which folders to import from.

## `trash/`

**The server shouldn't use the contents of this directory.**

This directory contains deleted profiles.
The app stores each deleted profile as a `.zip` file,
named using the Unix millisecond timestamp at the time of deletion.
Each trash entry contains all the files in the profile, all at the root.
