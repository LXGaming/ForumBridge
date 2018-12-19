/*
 * Copyright 2018 lolnet.co.nz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.co.lolnet.forumbridge.common;

import nz.co.lolnet.forumbridge.common.util.Logger;

public class ForumBridge {
    
    private static ForumBridge instance;
    private final Logger logger;
    
    public ForumBridge() {
        instance = this;
        this.logger = new Logger();
    }
    
    public static ForumBridge getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
}