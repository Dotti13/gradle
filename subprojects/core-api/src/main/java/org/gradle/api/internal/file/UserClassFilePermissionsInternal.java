/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.file;

import org.gradle.api.file.FilePermissions;
import org.gradle.api.file.UserClassFilePermissions;

public interface UserClassFilePermissionsInternal extends UserClassFilePermissions {

    /**
     * Sets the permission for a specific class of users from a complete Unix-style permission.
     * See {@link FilePermissions#unix(String)} for details.
     *
     * @param permission complete Unix-style permission for all users
     * @param index index of the part specific to this user group (so 0 for user, 1 for group, 2 for others)
     */
    void unix(String permission, int index);

    /**
     * Sets the permission for a specific class of users based on a partial Unix-style mode
     * (i.e. a number between 0 and 7).
     * See {@link FilePermissions#unix(String)} for details.
     *
     * @param unixNumeric partial Unix-style numeric permission for a specific class of users
     */
    void unix(int unixNumeric);

}
